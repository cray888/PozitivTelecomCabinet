package ru.pozitivtelecom.cabinet.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.adapters.TariffServiceAdapter;
import ru.pozitivtelecom.cabinet.models.MainModel;
import ru.pozitivtelecom.cabinet.models.TariffServiceModel;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.soap.SoapClass;

public class TariffServiceFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //Private preference
    private int mTypeData;
    private LinearLayoutManager mLayoutManager;
    private List<TariffServiceModel> mElementList;
    private Type itemsListType = new TypeToken<List<TariffServiceModel>>(){}.getType();
    private TariffServiceAdapter mAdapter;

    //Public preference

    //UI preference
    private ViewGroup mView;
    private LinearLayout mMainView;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefresh;

    public TariffServiceFragment() {}

    public static TariffServiceFragment newInstance(int type) {
        TariffServiceFragment fragment = new TariffServiceFragment();
        Bundle args = new Bundle();
        args.putInt("datatype", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTypeData = getArguments().getInt("datatype");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (ViewGroup)inflater.inflate(R.layout.fragment_tariff_service, container, false);

        mProgressDialog = new ProgressDialog(mView.getContext());
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mMainView = mView.findViewById(R.id.mainView);

        mLayoutManager = new LinearLayoutManager(mView.getContext());
        mRecyclerView = mView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefresh = mView.findViewById(R.id.swipelayout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(
                R.color.blue_swipe, R.color.green_swipe,
                R.color.orange_swipe, R.color.red_swipe);

        UpdateData();
        return mView;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(false);
        UpdateData();
    }

    public void UpdateData() {
        mProgressDialog.show();

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", "");
        mProperty.put("command", mTypeData == 1? "Tariff": "Service");
        mProperty.put("data", "");

        SoapClass soapObject = new SoapClass("GetData", mProperty);
        soapObject.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {}

            @Override
            public void onComplete(String Result) {
                mProgressDialog.dismiss();

                final MainModel resultClass = new Gson().fromJson(Result, MainModel.class);

                if (!resultClass.Error) {
                    mElementList = new Gson().fromJson(new Gson().toJson(resultClass.Data), itemsListType);
                    mAdapter = new TariffServiceAdapter(mView.getContext(), mElementList);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mMainView, resultClass.Message, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String Result) {
                mProgressDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(mMainView, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
        soapObject.execute();
    }
}
