package embla3d.engine.items;

import embla3d.engine.meshs.Material;
import embla3d.engine.meshs.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class MyItem {
    /**
     * Construct a item with the specified mesh, scale, rotation, translation and
     * an item can be in several groups of items
     *
     * @param appearance     Vertex array
     * @param scale          float
     * @param rotation       Vector3f
     * @param translation    Vector3f
     * @param groups         ArrayList<ItemGroup>
     */
    private Mesh appearance;
    private float scale;
    private Vector3f rotation;
    private Vector3f translation;
    private ArrayList<ItemGroup> groups;

    //Constructor
    public MyItem(Mesh appearance, float scale, Vector3f rotation, Vector3f position) {
        super();
        this.appearance = appearance;
        this.scale = scale;
        this.rotation = rotation;
        this.translation = position;
        this.groups = new ArrayList<>();
    }

    public MyItem(MyItem source) {
        this.appearance = source.appearance;
        this.scale = source.scale;
        this.rotation = new Vector3f(source.rotation);
        this.translation = new Vector3f(source.translation);
        this.groups = new ArrayList<>();
    }

    public MyItem(Mesh m) {
        this.appearance = m;
        scale = 1f;
        rotation = new Vector3f();
        translation = new Vector3f();
        this.groups = new ArrayList<>();
    }

    public MyItem clone() {

        return new MyItem(this);
    }

    // OpenGl function
    public Matrix4f getWorldMatrix() {
        return new Matrix4f().identity().translate(translation).
                rotateX((float) Math.toRadians(rotation.x)).
                rotateY((float) Math.toRadians(rotation.y)).
                rotateZ((float) Math.toRadians(rotation.z)).
                scale(scale);
    }

    //Scale
    public float getScale() {
        return scale;
    }

    public void setScale(float val) {
        scale = val;
    }

    public void rotate(float x, float y, float z) {
        this.setRotation(getRotation().add(x, y, z));
    }

    //Rotation
    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x, y, z), null);
    }

    public void setPosition(Vector3f pos, ItemGroup g) {
        this.translation = pos;
        for (ItemGroup gr : groups) {
            if (gr != g)
                gr.updateCenter();
        }
    }

    public void translate(float x, float y, float z) {
        translate(x, y, z, null);
    }

    public void translate(float x, float y, float z, ItemGroup g) {
        Vector3f old = getPosition(), vect = new Vector3f(x + old.x, y + old.y, z + old.z);
        this.setPosition(vect, g);
    }

    //Translation
    public Vector3f getPosition() {
        return translation;
    }

    public void setPosition(Vector3f pos) {
        setPosition(pos, null);
    }

    //Group Moves
    public void revolveAround(Vector3f center, float degX, float degY, float degZ) {
        Vector4f pos = new Vector4f(translation, 1f);
        pos.add(-center.x, -center.y, -center.z, 0);
        Matrix4f trans = new Matrix4f();
        trans.rotateX((float) Math.toRadians(degX));
        trans.rotateY((float) Math.toRadians(degY));
        trans.rotateZ((float) Math.toRadians(degZ));
        trans.transform(pos);
        pos.add(center.x, center.y, center.z, 0);
        translation = new Vector3f(pos.x, pos.y, pos.z);
    }

    public void repelBy(Vector3f center, float dist) {
        Vector3f dif = new Vector3f(translation.x - center.x, translation.y - center.y, translation.z - center.z);
        float norm = dif.length();
        if (norm != 0) {
            float move = (dist / norm) + 1;
            dif.mul(move);
            dif.add(center);
            translation = dif;
        }
    }

    // Groups Management
    public ArrayList<ItemGroup> getGroups() {
        return groups;
    }

    // Don't use in Clojure addToGroup and removeFromGroup
    public void addToGroup(ItemGroup g) {
        groups.add(g);
    }

    public void removeFromGroup(ItemGroup g) {
        groups.remove(g);
    }

    // Input Function
    public void update() {
    }

    // Material getter
    public void setColor(float r, float g, float b) {
        this.getAppearance().setMaterial(new Material(new Vector3f(r, g, b), 0.f));
    }

    public Mesh getAppearance() {
        return appearance;
    }

    public float getReflectance() {
        return this.getAppearance().getMaterial().reflectance;
    }

    public void setReflectance(float refl) {
        this.getAppearance().getMaterial().reflectance = refl;
    }

    public Vector3f getColor() {
        return this.getAppearance().getMaterial().color;
    }

    public void setColor(Vector3f color) {
        this.getAppearance().setMaterial(new Material(color, 0.f));
    }
}
