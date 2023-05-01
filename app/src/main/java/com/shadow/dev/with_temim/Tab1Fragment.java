package com.shadow.dev.with_temim;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Tab1Fragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    private static final String Tag="Tab1Fragment";
    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private DialogClass dialogClass;
    private  TextView textView;
    private com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButtonConx;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.tab1_fragment,container,false);
        textView = view.findViewById(R.id.idConnexiontAlert);
        textView.setVisibility(View.INVISIBLE);

        //Todo set your action in this tab here
        dialogClass=new DialogClass(getContext(),getActivity());
        final VideoYtList videoYt = new VideoYtList(getContext());
        ArrayList<Object> arrayList;
        arrayList=videoYt.getVideoList();


        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.rvVidioShowMain);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
        //        mLayoutManager.getOrientation());
        //recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MyRecyclerViewAdapter(getContext(), arrayList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        //set Floating Action menu items and actions
        FloatingActionButton floatingActionButton1 = view.findViewById(R.id.material_design_floating_action_menu_item1_tab1);
        FloatingActionButton floatingActionButton2 = view.findViewById(R.id.material_design_floating_action_menu_item2_tab1);
        FloatingActionButton floatingActionButton3 = view.findViewById(R.id.material_design_floating_action_menu_item3_tab1);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                dialogClass.dialogLauncher(R.string.share_title,R.string.share_message,1);
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                dialogClass.dialogLauncher(R.string.rate_title,R.string.rate_message,2);
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                dialogClass.exitApplicationDialog();
            }
        });

        floatingActionButtonConx = view.findViewById(R.id.idConnectionBu);
        floatingActionButtonConx.hide();
        floatingActionButtonConx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
               videoYt.stratListVideosExecute();

            //Toast.makeText(getContext(),"No internet Connexion!",Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }


    //Get Connectivity State
    private boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
    }


    public static String  VIDEO_POSITION = "position_yt_video";
    public static String  VIDEO_ID = "id_yt_video";
    public static String  VIDEO_Title = "title_yt_video";
    private static int ADS=1;
    @Override
    public void onItemClick(View view, int position) {
        if(adapter.getItemViewType(position)==ADS)return;

            Intent intent = new Intent(getActivity(), MainActivity.class);
            //intent.putExtra(VIDEO_POSITION, position);
            Bundle extras=new Bundle();
            extras.putString(VIDEO_ID,adapter.getItem(position).youtubeUrl);
            extras.putString(VIDEO_Title,adapter.getItem(position).videoTitle);
            extras.putInt(VIDEO_POSITION,position);
            intent.putExtras(extras);
            startActivity(intent);
            // getActivity().finish();
    }



    private static String url = "https://api.npoint.io/dcb6e1ac4215ea78da7b";
    public class VideoYtList  {

        private ArrayList<Object> videoYt;
        private Context mContext;
        private ProgressDialog pDialog;


        VideoYtList(Context context ) {
            this.mContext=context;
            videoYt = new ArrayList<>();
            new GetVideoIdAndTitle().execute();
            //videoYt.add(new AdsObject());
        }

        public final void stratListVideosExecute(){
            new GetVideoIdAndTitle().execute();
        }

        public ArrayList<Object> getVideoList(){
            return videoYt;
        }

        private static final String IMAGE_BASE_URL = "http://i.ytimg.com/vi/";

        // Max Res
        private String getThumbUrl(String _videoId) {
            return IMAGE_BASE_URL + _videoId + "/maxresdefault.jpg";
        }


        /**
         * Async task class to get json by making HTTP call
         */

        private class GetVideoIdAndTitle extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                // Showing progress dialog
                pDialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
                pDialog.setMessage(getResources().getString(R.string.plz_wait));
                pDialog.setCancelable(false);
                pDialog.show();
            }

            private boolean isNotConnected=false;
            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONObject info = jsonObj.getJSONObject("info");
                        JSONArray videoTab = info.getJSONArray("TabVideos");
                        // looping through All Contacts
                        for (int i = 0; i < videoTab.length(); i++) {
                            JSONObject c = videoTab.getJSONObject(i);

                            String idVideo = c.getString("idVideo");
                            String titel = c.getString("titel");

                            if (i%8==0) {
                                videoYt.add(new AdsObject());
                            }
                            videoYt.add(new ItemsList(idVideo,titel, getThumbUrl(idVideo)));

                        }
                    } catch (JSONException e) {

                    }
                    isNotConnected = false;
                }else {
                    isNotConnected = true;
                }

                return null;
            }


            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Dismiss the progress dialog
                if (pDialog.isShowing()){
                    pDialog.dismiss();
                }

                 if(isNotConnected) {
                     dialogClass.connexionDialog();
                     textView.setVisibility(View.VISIBLE);
                     floatingActionButtonConx.show();

                     return;
                 }


                textView.setVisibility(View.INVISIBLE);
                floatingActionButtonConx.hide();
                recyclerView.setAdapter(adapter);
            }
        }

    }

}
