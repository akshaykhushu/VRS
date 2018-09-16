package com.example.aksha.newb;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.SyncStateContract;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public  class ClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {
    Context context;
    ClusterManager<T> clusterManager;
    ClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.clusterManager = clusterManager;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
        //start clustering if 2 or more items overlap
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(T item,
                                               MarkerOptions markerOptions) {
        MarkerInfo markerInfo = (MarkerInfo) item;
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
        iconGenerator.setTextAppearance(R.style.iconGenText);
        Bitmap iconBitmap = iconGenerator.makeIcon(markerInfo.getTitle() + " | " + markerInfo.getCost());
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(iconBitmap);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
    }

    @Override
    protected void onClusterItemRendered(T clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);

    }

    @Override
    protected void onClusterRendered(Cluster<T> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
        iconGenerator.setTextAppearance(R.style.iconGenText);
        Bitmap iconBitmap = iconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
//        marker.setVisible(false);
    }
}