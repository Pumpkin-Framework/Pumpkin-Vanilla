package nl.jk_5.pumpkin.server.util.event.generator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates event implementations by generating the necessary event class
 * and event factory at runtime.
 */
public class ClassGeneratorProvider implements FactoryProvider {

    private final LocalClassLoader classLoader = new LocalClassLoader(ClassGeneratorProvider.class.getClassLoader());
    private final ClassGenerator builder = new ClassGenerator();
    private final String targetPackage;

    /**
     * Create a new instance.
     *
     * @param targetPackage The target package to place generated event classes in
     */
    public ClassGeneratorProvider(String targetPackage) {
        checkNotNull(targetPackage, "targetPackage");
        this.targetPackage = targetPackage;
    }

    @Override
    public NullPolicy getNullPolicy() {
        return this.builder.getNullPolicy();
    }

    @Override
    public void setNullPolicy(NullPolicy nullPolicy) {
        this.builder.setNullPolicy(nullPolicy);
    }

    /**
     * Get the canonical name used for a generated event class.
     *
     * @param clazz The class
     * @param classifier The classifier
     * @return Canonical name
     */
    protected String getClassName(Class<?> clazz, String classifier) {
        return this.targetPackage + "." + clazz.getSimpleName() + "$" + classifier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> EventFactory<T> create(final Class<T> type, Class<?> parentType) {
        String eventName = getClassName(type, "Impl");
        String factoryName = getClassName(type, "Factory");

        Class<?> eventClass = this.classLoader.defineClass(eventName, this.builder.createClass(type, eventName, parentType));
        Class<?> factoryClass = this.classLoader.defineClass(factoryName, this.builder.createFactory(eventClass, factoryName));

        try {
            return (EventFactory<T>) factoryClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to create event factory", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create event factory", e);
        }
    }

    /**
     * Class loader to use to call {@link #defineClass(String, byte[])}.
     */
    private static class LocalClassLoader extends ClassLoader {

        public LocalClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

}
