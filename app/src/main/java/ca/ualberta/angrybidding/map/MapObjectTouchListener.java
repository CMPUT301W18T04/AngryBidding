package ca.ualberta.angrybidding.map;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.slouple.android.Pointer;
import com.slouple.android.input.MultiTouchListener;

public class MapObjectTouchListener extends MultiTouchListener {
    protected MapObject mapObject;

    public MapObjectTouchListener(Context context, MapObject mapObject, View view) {
        super(context, view);
        this.mapObject = mapObject;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(mapObject.getMapView() instanceof TouchableMapView){
            ((TouchableMapView)mapObject.getMapView()).touchListener.setReferenceView(view);
            super.onTouchEvent(event);
            mapObject.getMapView().dispatchTouchEvent(event);
            ((TouchableMapView)mapObject.getMapView()).touchListener.setReferenceView(null);
            return true;
        }else{
            return super.onTouchEvent(event);
        }
    }

    public MapObject getMapObject(){
        return mapObject;
    }

    @Override
    public boolean onPointerAdded(Pointer pointer){

        return super.onPointerAdded(pointer);
    }

    @Override
    public boolean onPointerRemoved(Pointer pointer){
        return super.onPointerRemoved(pointer);
    }

    @Override
    public boolean onPointerMoved(Pointer pointer){
        return super.onPointerMoved(pointer);
    }
}
