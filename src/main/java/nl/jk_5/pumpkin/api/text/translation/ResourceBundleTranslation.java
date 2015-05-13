package nl.jk_5.pumpkin.api.text.translation;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A translation class designed to be used for ResourceBundles. For convenience, most users will want to wrap this in a class that keeps track of
 * resource bundles. A simple implementation would look like:
 * <pre>
 *     public class TranslationHelper {
 *         private static final Function&lt;Locale, ResourceBundle&gt; LOOKUP_FUNC = new Function&lt;Locale, ResourceBundle&gt;() {
 *             &at;Nullable
 *             &at;Override
 *             public ResourceBundle apply(Locale input) {
 *                return ResourceBundle.getBundle("com.mydomain.myplugin.Translations", input);
 *             }
 *         };
 *
 *         private TranslationHelper() {} // Prevent instance creation
 *
 *         public static Text t(String key, Object... args) {
 *             return Texts.of(new ResourceBundleTranslation(key, LOOKUP_FUNC), args);
 *         }
 *     }
 *
 * </pre>
 */
public class ResourceBundleTranslation implements Translation {
    private final String key;
    private final Function<Locale, ResourceBundle> bundleFunction;

    /**
     * Create a ResourceBundle-backed translation for the given key and bundle factory.
     *
     * @param key The key to use
     * @param bundleFunction The bundle function to get a bundle from
     */
    public ResourceBundleTranslation(String key, Function<Locale, ResourceBundle> bundleFunction) {
        this.key = key;
        this.bundleFunction = bundleFunction;
    }

    @Override
    public String getId() {
        return this.key;
    }

    @Override
    public String get(Locale locale) {
        Preconditions.checkNotNull(locale, "locale");
        try {
            ResourceBundle bundle = this.bundleFunction.apply(locale);
            return bundle == null ? this.key : bundle.getString(this.key);
        } catch (MissingResourceException ex) {
            return this.key;
        }
    }

    @Override
    public String get(Locale locale, Object... args) {
        return String.format(locale, get(locale), args);
    }
}
