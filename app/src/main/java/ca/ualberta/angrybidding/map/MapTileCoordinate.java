package ca.ualberta.angrybidding.map;

public class MapTileCoordinate {
    private int x, y, z;

    public MapTileCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MapTileCoordinate) {
            return ((MapTileCoordinate) other).getX() == this.getX() &&
                    ((MapTileCoordinate) other).getY() == this.getY() &&
                    ((MapTileCoordinate) other).getZ() == this.getZ();
        }
        return false;
    }

    public boolean isParent(MapTileCoordinate parent) {
        return parent.equals(getParentCoordinate(parent.getZ()));
    }

    public MapTileCoordinate[] getChildCoordinate(int childZ) {
        if (childZ >= this.z) {
            return null;
        }
        int multiplier = (int) Math.pow(2, childZ - this.z);
        MapTileCoordinate[] childCoords = new MapTileCoordinate[multiplier * multiplier];
        int index = 0;
        for (int x = 0; x < multiplier; x++) {
            for (y = 0; y < multiplier; y++) {
                childCoords[index] = new MapTileCoordinate(getX() * multiplier + x, getY() * multiplier + y, childZ);
                index++;
            }
        }
        return childCoords;
    }

    public MapTileCoordinate getParentCoordinate(int parentZ) {
        if (parentZ >= this.z) {
            return null;
        }
        int multiplier = (int) Math.pow(2, this.z - parentZ);
        int parentX = getX() / multiplier;
        int parentY = getY() / multiplier;
        MapTileCoordinate parentCoord = new MapTileCoordinate(parentX, parentY, parentZ);
        return parentCoord;
    }
}
