package nl.jk_5.pumpkin.launch.transformer.util;

import com.google.common.base.Objects;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class Mapping {

    public String owner;
    public String name;
    public String desc;

    public Mapping(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;

        if(this.owner.contains(".")){
            throw new IllegalArgumentException(this.owner);
        }
    }

    public boolean matches(MethodNode node) {
        return this.name.equals(node.name) && this.desc.equals(node.desc);
    }

    public boolean matches(FieldNode node) {
        return this.name.equals(node.name) && this.desc.equals(node.desc);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Mapping)){
            return false;
        }

        Mapping desc = (Mapping) obj;
        return this.owner.equals(desc.owner) && this.name.equals(desc.name) && this.desc.equals(desc.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.desc, this.name, this.owner);
    }

    @Override
    public String toString() {
        if(this.name.length() == 0){
            return "[" + this.owner + "]";
        }
        if(this.desc.length() == 0){
            return "[" + this.owner + "." + this.name + "]";
        }
        return "[" + (isMethod() ? methodDesc() : fieldDesc()) + "]";
    }

    public String methodDesc() {
        return this.owner + "." + this.name + this.desc;
    }

    public String fieldDesc() {
        return this.owner + "." + this.name + ":" + this.desc;
    }

    public boolean isClass() {
        return this.name.length() == 0;
    }

    public boolean isMethod() {
        return this.desc.contains("(");
    }

    public boolean isField() {
        return !isClass() && !isMethod();
    }

    public Mapping map(Remapper mapper) {
        if(isMethod()){
            this.name = mapper.mapMethodName(this.owner, this.name, this.desc);
        }else if(isField()){
            this.name = mapper.mapFieldName(this.owner, this.name, this.desc);
        }

        this.owner = mapper.mapType(this.owner);

        if(isMethod()){
            this.desc = mapper.mapMethodDesc(this.desc);
        }else if(this.desc.length() > 0){
            this.desc = mapper.mapDesc(this.desc);
        }

        return this;
    }
}
