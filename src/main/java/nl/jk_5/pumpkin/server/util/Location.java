package nl.jk_5.pumpkin.server.util;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Represents a 3-dimensional location in a world
 */
public class Location {

    private final World world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    /**
     * Constructs a new Location with the given coordinates
     *
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     */
    public Location(final double x, final double y, final double z) {
        this(null, x, y, z, 0, 0);
    }

    /**
     * Constructs a new Location with the given coordinates and direction
     *
     * @param x     The x-coordinate of this new location
     * @param y     The y-coordinate of this new location
     * @param z     The z-coordinate of this new location
     * @param yaw   The absolute rotation on the x-plane, in degrees
     * @param pitch The absolute rotation on the y-plane, in degrees
     */
    public Location(final double x, final double y, final double z, final float yaw, final float pitch) {
        this(null, x, y, z, yaw, pitch);
    }

    /**
     * Constructs a new Location from {@link BlockPos}
     *
     * @param coords The {@link }} to copy
     */
    public Location(final BlockPos coords) {
        this(null, coords.getX(), coords.getY(), coords.getZ(), 0, 0);
    }

    /**
     * Constructs a new Location from {@link BlockPos}
     *
     * @param coords The {@link BlockPos} to copy
     * @param yaw    The absolute rotation on the x-plane, in degrees
     * @param pitch  The absolute rotation on the y-plane, in degrees
     */
    public Location(final BlockPos coords, final float yaw, final float pitch) {
        this(null, coords.getX(), coords.getY(), coords.getZ(), yaw, pitch);
    }

    /**
     * Constructs a new Location with the given coordinates
     *
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     */
    public Location(World world, final double x, final double y, final double z) {
        this(world, x, y, z, 0, 0);
    }

    /**
     * Constructs a new Location with the given coordinates and direction
     *
     * @param x     The x-coordinate of this new location
     * @param y     The y-coordinate of this new location
     * @param z     The z-coordinate of this new location
     * @param yaw   The absolute rotation on the x-plane, in degrees
     * @param pitch The absolute rotation on the y-plane, in degrees
     */
    public Location(World world, final double x, final double y, final double z, final float yaw, final float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * Constructs a new Location from {@link BlockPos}
     *
     * @param coords The {@link }} to copy
     */
    public Location(World world, final BlockPos coords) {
        this(world, coords.getX(), coords.getY(), coords.getZ(), 0, 0);
    }

    /**
     * Constructs a new Location from {@link BlockPos}
     *
     * @param coords The {@link BlockPos} to copy
     * @param yaw    The absolute rotation on the x-plane, in degrees
     * @param pitch  The absolute rotation on the y-plane, in degrees
     */
    public Location(World world, final BlockPos coords, final float yaw, final float pitch) {
        this(world, coords.getX(), coords.getY(), coords.getZ(), yaw, pitch);
    }

    /**
     * Sets the x-coordinate of this point
     *
     * @param x X-coordinate
     */
    public Location setX(double x) {
        return new Location(this.world, x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * Gets the x-coordinate of this point
     *
     * @return x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the floored value of the X component, indicating the block that
     * this point is contained with.
     *
     * @return block X
     */
    public int getBlockX() {
        return MathUtil.floor(x);
    }

    /**
     * Sets the y-coordinate of this point
     *
     * @param y y-coordinate
     */
    public Location setY(double y) {
        return new Location(this.world, this.x, y, this.z, this.yaw, this.pitch);
    }

    /**
     * Gets the y-coordinate of this point
     *
     * @return y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the floored value of the Y component, indicating the block that
     * this point is contained with.
     *
     * @return block y
     */
    public int getBlockY() {
        return MathUtil.floor(y);
    }

    /**
     * Sets the z-coordinate of this point
     *
     * @param z z-coordinate
     */
    public Location setZ(double z) {
        return new Location(this.world, this.x, this.y, z, this.yaw, this.pitch);
    }

    /**
     * Gets the z-coordinate of this point
     *
     * @return z-coordinate
     */
    public double getZ() {
        return z;
    }

    /**
     * Gets the floored value of the Z component, indicating the block that
     * this point is contained with.
     *
     * @return block z
     */
    public int getBlockZ() {
        return MathUtil.floor(z);
    }

    public World getWorld() {
        return world;
    }

    public Location setWorld(World world){
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * Sets the yaw of this location, measured in degrees.
     * <ul>
     * <li>A yaw of 0 or 360 represents the positive z direction.
     * <li>A yaw of 180 represents the negative z direction.
     * <li>A yaw of 90 represents the negative x direction.
     * <li>A yaw of 270 represents the positive x direction.
     * </ul>
     * Increasing yaw values are the equivalent of turning to your
     * right-facing, increasing the scale of the next respective axis, and
     * decreasing the scale of the previous axis.
     *
     * @param yaw new rotation's yaw
     */
    public Location setYaw(float yaw) {
        return new Location(this.world, this.x, this.y, this.z, yaw, this.pitch);
    }

    /**
     * Gets the yaw of this location, measured in degrees.
     * <ul>
     * <li>A yaw of 0 or 360 represents the positive z direction.
     * <li>A yaw of 180 represents the negative z direction.
     * <li>A yaw of 90 represents the negative x direction.
     * <li>A yaw of 270 represents the positive x direction.
     * </ul>
     * Increasing yaw values are the equivalent of turning to your
     * right-facing, increasing the scale of the next respective axis, and
     * decreasing the scale of the previous axis.
     *
     * @return the rotation's yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the pitch of this location, measured in degrees.
     * <ul>
     * <li>A pitch of 0 represents level forward facing.
     * <li>A pitch of 90 represents downward facing, or negative y
     * direction.
     * <li>A pitch of -90 represents upward facing, or positive y direction.
     * <ul>
     * Increasing pitch values the equivalent of looking down.
     *
     * @param pitch new incline's pitch
     */
    public Location setPitch(float pitch) {
        return new Location(this.world, this.x, this.y, this.z, this.yaw, pitch);
    }

    /**
     * Sets the pitch of this location, measured in degrees.
     * <ul>
     * <li>A pitch of 0 represents level forward facing.
     * <li>A pitch of 90 represents downward facing, or negative y
     * direction.
     * <li>A pitch of -90 represents upward facing, or positive y direction.
     * <ul>
     * Increasing pitch values the equivalent of looking down.
     *
     * @return the incline's pitch
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Adds the location by another.
     *
     * @param vec The other location
     * @return the same location
     * @throws NullPointerException when vec is null
     */
    public Location add(@Nonnull Location vec) {
        Preconditions.checkNotNull(vec, "vec");
        return new Location(this.world, this.x + vec.x, this.y + vec.y, this.z + vec.z);
    }

    /**
     * Adds the location by another. Not world-aware.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the new location
     */
    public Location add(double x, double y, double z) {
        return new Location(this.world, this.x + x, this.y + y, this.z + z);
    }

    /**
     * Subtracts the location by another.
     *
     * @param vec The other location
     * @return the new location
     * @throws NullPointerException when vec is null
     */
    public Location subtract(@Nonnull Location vec) {
        Preconditions.checkNotNull(vec, "vec");
        return new Location(this.world, this.x - vec.x, this.y - vec.y, this.z - vec.z);
    }

    /**
     * Subtracts the location by another. Not world-aware and
     * orientation independent.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the same location
     */
    public Location subtract(double x, double y, double z) {
        return new Location(this.world, this.x - x, this.y - y, this.z - z);
    }

    /**
     * Get the distance between this location and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the location's magnitude. NaN will
     * be returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other location
     * @return the distance
     * @throws NullPointerException when o is null
     */
    public double distance(@Nonnull Location o) {
        Preconditions.checkNotNull(o, "o");
        return Math.sqrt(distanceSquared(o));
    }

    /**
     * Get the squared distance between this location and another.
     *
     * @param o The other location
     * @return the squared distance
     * @throws NullPointerException when o is null
     */
    public double distanceSquared(@Nonnull Location o) {
        Preconditions.checkNotNull(o, "o");
        return MathUtil.square(x - o.x) + MathUtil.square(y - o.y) + MathUtil.square(z - o.z);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same location
     */
    public Location multiply(double m) {
        return new Location(this.world, this.x * m, this.y * m, this.z * m, this.yaw, this.pitch);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Float.compare(location.pitch, pitch) != 0) return false;
        if (Double.compare(location.x, x) != 0) return false;
        if (Double.compare(location.y, y) != 0) return false;
        if (Float.compare(location.yaw, yaw) != 0) return false;
        if (Double.compare(location.z, z) != 0) return false;
        if (world != null ? !world.equals(location.world) : location.world != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = world != null ? world.hashCode() : 0;
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("world", world)
                .add("x", x)
                .add("y", y)
                .add("z", z)
                .add("yaw", yaw)
                .add("pitch", pitch)
                .toString();
    }

    public Location copy() {
        return new Location(this.world, this.getX(), this.getY(), this.getZ(), this.pitch, this.yaw);
    }
}
