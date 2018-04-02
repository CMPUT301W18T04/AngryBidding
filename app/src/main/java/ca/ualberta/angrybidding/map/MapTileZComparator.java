package ca.ualberta.angrybidding.map;

import java.util.Comparator;

public class MapTileZComparator implements Comparator<MapTile>{
    private boolean ascend;
    public MapTileZComparator(boolean ascend){
        this.ascend = ascend;
    }
    @Override
    public int compare(MapTile x, MapTile y) {
        if(ascend){
            MapTile tmpX = x;
            x = y;
            y = tmpX;
        }
        if(x.getZ() != y.getZ()){
            return Integer.signum(x.getZ() - y.getZ());
        }else{
            if(x.getX() != y.getX()){
                return Integer.signum(x.getX() - y.getX());
            }else{
                return Integer.signum(x.getY() - y.getY());
            }
        }
    }
}
