package com.example.booki;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderTracking extends FragmentActivity implements OnMapReadyCallback {

    TextView tvOrderId, tvEta, tvLiveStatus;
    TextView tvDeliveryAddress, tvPaymentId, tvAmountPaid, tvOrderPlacedTime;
    CardView btnBackTracking, btnNeedHelp;

    // Step icons
    CardView iconConfirmed, iconShipped, iconOutForDelivery, iconDelivered;

    // Step lines
    android.view.View lineConfirmed, lineShipped, lineOutForDelivery;

    String orderId, paymentId;
    int totalAmount;

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleMap mMap;

    enum Status { ORDER_PLACED, CONFIRMED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED }

    // Simulated current status — in real app fetch from Firestore
    Status currentStatus = Status.CONFIRMED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get data from intent
        orderId     = getIntent().getStringExtra("orderId");
        paymentId   = getIntent().getStringExtra("paymentId");
        totalAmount = getIntent().getIntExtra("totalAmount", 0);

        bindViews();
        setupMap();
        populateData();
        updateStepperUI(currentStatus);

        btnBackTracking.setOnClickListener(v -> finish());

        btnNeedHelp.setOnClickListener(v ->
                Toast.makeText(this, "Support: support@booki.com", Toast.LENGTH_LONG).show()
        );
    }

    private void bindViews() {
        tvOrderId          = findViewById(R.id.tvOrderId);
        tvEta              = findViewById(R.id.tvEta);
        tvLiveStatus       = findViewById(R.id.tvLiveStatus);
        tvDeliveryAddress  = findViewById(R.id.tvDeliveryAddress);
        tvPaymentId        = findViewById(R.id.tvPaymentId);
        tvAmountPaid       = findViewById(R.id.tvAmountPaid);
        tvOrderPlacedTime  = findViewById(R.id.tvOrderPlacedTime);
        btnBackTracking    = findViewById(R.id.btnBackTracking);
        btnNeedHelp        = (CardView) findViewById(R.id.btnNeedHelp);

        iconConfirmed       = findViewById(R.id.iconConfirmed);
        iconShipped         = findViewById(R.id.iconShipped);
        iconOutForDelivery  = findViewById(R.id.iconOutForDelivery);
        iconDelivered       = findViewById(R.id.iconDelivered);

        lineConfirmed       = findViewById(R.id.lineConfirmed);
        lineShipped         = findViewById(R.id.lineShipped);
        lineOutForDelivery  = findViewById(R.id.lineOutForDelivery);
    }

    private void populateData() {
        // Order ID
        String shortId = orderId != null
                ? "#" + orderId.substring(0, Math.min(8, orderId.length())).toUpperCase()
                : "#UNKNOWN";
        tvOrderId.setText("Order " + shortId);

        // Payment ID
        tvPaymentId.setText(paymentId != null ? paymentId : "—");

        // Amount
        tvAmountPaid.setText("₹ " + totalAmount);

        // Placed time
        String time = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                .format(new Date());
        tvOrderPlacedTime.setText(time);

        // Load delivery address from Firestore
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(doc -> {
                        String address = doc.getString("address");
                        tvDeliveryAddress.setText(
                                address != null && !address.isEmpty()
                                        ? address
                                        : "Address not available"
                        );
                    });
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Apply dark map style
        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
        } catch (Exception e) {
            // Dark style not available — continue with default
        }

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


        LatLng deliveryAgent = new LatLng(28.6139, 77.2090); // Delhi example
        LatLng destination   = new LatLng(28.6200, 77.2150); // Buyer location

        mMap.addMarker(new MarkerOptions()
                .position(deliveryAgent)
                .title("Delivery Agent")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


        mMap.addCircle(new CircleOptions()
                .center(destination)
                .radius(300)
                .fillColor(0x22E8B84B)
                .strokeColor(0x88E8B84B)
                .strokeWidth(2));


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deliveryAgent, 14f));
    }

    private void updateStepperUI(Status status) {

        int accentColor  = getResources().getColor(R.color.accent, null);
        int elevatedColor = getResources().getColor(R.color.bg_elevated, null);
        int borderColor  = getResources().getColor(R.color.border, null);
        int primaryText  = getResources().getColor(R.color.text_primary, null);
        int secondaryText = getResources().getColor(R.color.text_secondary, null);


        boolean confirmedDone = status.ordinal() >= Status.CONFIRMED.ordinal();
        iconConfirmed.setCardBackgroundColor(confirmedDone ? accentColor : elevatedColor);
        lineConfirmed.setBackgroundColor(confirmedDone ? accentColor : borderColor);
        updateStepText(R.id.stepConfirmed, confirmedDone ? primaryText : secondaryText);


        boolean shippedDone = status.ordinal() >= Status.SHIPPED.ordinal();
        iconShipped.setCardBackgroundColor(shippedDone ? accentColor : elevatedColor);
        setIconText(iconShipped, shippedDone ? "✓" : "3",
                shippedDone ? getResources().getColor(R.color.text_on_accent, null) : secondaryText);
        lineShipped.setBackgroundColor(shippedDone ? accentColor : borderColor);


        boolean outDone = status.ordinal() >= Status.OUT_FOR_DELIVERY.ordinal();
        iconOutForDelivery.setCardBackgroundColor(outDone ? accentColor : elevatedColor);
        setIconText(iconOutForDelivery, outDone ? "✓" : "4",
                outDone ? getResources().getColor(R.color.text_on_accent, null) : secondaryText);
        lineOutForDelivery.setBackgroundColor(outDone ? accentColor : borderColor);


        boolean deliveredDone = status.ordinal() >= Status.DELIVERED.ordinal();
        iconDelivered.setCardBackgroundColor(deliveredDone ? accentColor : elevatedColor);
        setIconText(iconDelivered, deliveredDone ? "✓" : "5",
                deliveredDone ? getResources().getColor(R.color.text_on_accent, null) : secondaryText);

        switch (status) {
            case ORDER_PLACED:
                tvLiveStatus.setText("Order Placed");
                tvEta.setText("5-7 Days");
                break;
            case CONFIRMED:
                tvLiveStatus.setText("Seller Confirmed");
                tvEta.setText("4-6 Days");
                break;
            case SHIPPED:
                tvLiveStatus.setText("Shipped");
                tvEta.setText("2-3 Days");
                break;
            case OUT_FOR_DELIVERY:
                tvLiveStatus.setText("Out for Delivery");
                tvEta.setText("Today");
                break;
            case DELIVERED:
                tvLiveStatus.setText("Delivered ✓");
                tvEta.setText("Done!");
                break;
        }
    }

    private void setIconText(CardView card, String text, int color) {
        if (card.getChildCount() > 0 && card.getChildAt(0) instanceof TextView) {
            TextView tv = (TextView) card.getChildAt(0);
            tv.setText(text);
            tv.setTextColor(color);
        }
    }

    private void updateStepText(int stepLayoutId, int color) {
        android.view.View stepLayout = findViewById(stepLayoutId);
        if (stepLayout instanceof android.view.ViewGroup) {


        }
    }
}