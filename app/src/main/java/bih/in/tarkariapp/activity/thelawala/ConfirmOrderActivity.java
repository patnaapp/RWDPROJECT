package bih.in.tarkariapp.activity.thelawala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import bih.in.tarkariapp.R;
import bih.in.tarkariapp.adaptor.ConfirmOrderAdaptor;
import bih.in.tarkariapp.adaptor.WorkReqrmntEntryAdapter;
import bih.in.tarkariapp.entity.GetVegEntity;
import bih.in.tarkariapp.entity.PlaceOrderResponse;
import bih.in.tarkariapp.utility.Utiilties;
import bih.in.tarkariapp.webService.Api;
import bih.in.tarkariapp.webService.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmOrderActivity extends AppCompatActivity {

    RecyclerView listView;
    TextView tv_Norecord;
    String userid;
    ArrayList<GetVegEntity> orderList;
    ConfirmOrderAdaptor adapter;
    String deliverydate="";
    Button buton_placeOrder_confrm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        listView = findViewById(R.id.listviewshow_cnfrm);
        buton_placeOrder_confrm = findViewById(R.id.buton_placeOrder_confrm);
        tv_Norecord = findViewById(R.id.tv_Norecord_cnfrm);
        deliverydate=getIntent().getStringExtra("delDate");
        userid= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("uid", "");
        orderList = (ArrayList<GetVegEntity>) getIntent().getSerializableExtra("orderlist");
        if (orderList.size()>0){
            populateData();
        }

        buton_placeOrder_confrm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmOrderActivity.this);
                alertDialogBuilder.setMessage("Are you sure,You want to place order");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                PlaceOrder();
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

    }


    public void populateData(){
        if(orderList != null && orderList.size()> 0){
            Log.e("data", ""+orderList.size());
            tv_Norecord.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new ConfirmOrderAdaptor(this, orderList);
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setAdapter(adapter);

        }else{
            listView.setVisibility(View.GONE);
            tv_Norecord.setVisibility(View.VISIBLE);
        }
    }

    public JsonArray getorder_json()
    {
        JsonArray orderarray= new JsonArray();
        for (GetVegEntity item:orderList)
        {
            JsonObject param = new JsonObject();
            param.addProperty("vegid", item.getVegid());
            param.addProperty("orderquantity", item.getVegQty());
            orderarray.add(param);
        }

        return orderarray;
    }


    private void PlaceOrder()
    {
        if(Utiilties.isOnline(ConfirmOrderActivity.this))
        {
            JsonObject param = new JsonObject();
            param.addProperty("telaid", userid);
            param.addProperty("Orderdate", deliverydate);
            // param.addProperty("lstVeg", getorder_json());
            param.add("lstVeg", getorder_json());

            Log.e("param", param.toString());

            final ProgressDialog dialog = new ProgressDialog(ConfirmOrderActivity.this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Uploading...");
            dialog.show();

            Api request = RetrofitClient.getRetrofitInstance().create(Api.class);

            Call<PlaceOrderResponse> call = null;

            call = request.PlaceOrderApi(param);



            call.enqueue(new Callback<PlaceOrderResponse>()
            {
                @Override
                public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response)
                {
                    if (dialog.isShowing()) dialog.dismiss();

                    PlaceOrderResponse userDetail = response.body();

                    if(userDetail != null)
                    {
                        // if(userDetail.getStatus() && (userDetail.getData().getRole().equals("MEMBER")||userDetail.getData().getRole().equals("THELA"))) {
                        if(userDetail.getStatus() ) {

                            AlertDialog.Builder ab = new AlertDialog.Builder(ConfirmOrderActivity.this);
                            ab.setTitle("सफल रहा");
                            ab.setMessage("आर्डर सफलतापूर्वक अपलोड हुआ");
                            ab.setPositiveButton("ओके", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    finish();
                                }
                            });

                            ab.create().getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                            ab.show();

                        }
                        else
                        {
                            Toast.makeText(ConfirmOrderActivity.this, userDetail.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(getContext(), response.body().getRoleName(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        AlertDialog.Builder ab = new AlertDialog.Builder(ConfirmOrderActivity.this);
                        ab.setTitle("Server Down");
                        ab.setMessage("Server Down, Please try again later!");
                        ab.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton)
                            {

                                dialog.dismiss();
                            }
                        });

                        ab.create().getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                        ab.show();
                    }

                }

                @Override
                public void onFailure(Call<PlaceOrderResponse> call, Throwable t)
                {
                    if (dialog.isShowing()) dialog.dismiss();
                    Toast.makeText(ConfirmOrderActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            showAlertDialog();
        }
    }

    public void showAlertDialog()
    {
        new AlertDialog.Builder(ConfirmOrderActivity.this)
                .setIcon(R.drawable.logo)
                .setTitle(R.string.app_name)
                .setMessage("Internet Connection is not avaliable..\nPlease Turn ON Network Connection")
                .setCancelable(false)
                .setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent I = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(I);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
