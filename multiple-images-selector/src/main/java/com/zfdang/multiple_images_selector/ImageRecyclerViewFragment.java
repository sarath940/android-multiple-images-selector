package com.zfdang.multiple_images_selector;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zfdang.multiple_images_selector.models.FolderListContent;
import com.zfdang.multiple_images_selector.models.ImageListContent;
import com.zfdang.multiple_images_selector.utilities.ScreenUtils;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnImageRecyclerViewInteractionListener}
 * interface.
 */
public class ImageRecyclerViewFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 3;
    private OnImageRecyclerViewInteractionListener mListener;
    private OnFolderRecyclerViewInteractionListener mFolderListener;

    private TextView mCategoryText;
    private PopupWindow mFolderPopupWindow;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ImageRecyclerViewFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ImageRecyclerViewFragment newInstance(int columnCount) {
        ImageRecyclerViewFragment fragment = new ImageRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selector_content, container, false);
        View rview = view.findViewById(R.id.image_recycerview);

        // Set the adapter
        if (rview instanceof RecyclerView) {
            Context context = rview.getContext();
            RecyclerView recyclerView = (RecyclerView) rview;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ImageRecyclerViewAdapter(ImageListContent.ITEMS, mListener));
        }


        mCategoryText = (TextView) view.findViewById(R.id.selector_image_folder_button);
        mCategoryText.setText(R.string.select_folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showAtLocation(view, Gravity.BOTTOM, 10, 150);
                    mFolderPopupWindow.update();
//                    int index = mFolderAdapter.getSelectIndex();
                    int index = 0;
                    index = index == 0 ? index : index - 1;
//                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });
        
        return view;
    }


    /**
     * http://stackoverflow.com/questions/23464232/how-would-you-create-a-popover-view-in-android-like-facebook-comments
     */
    private void createPopupFolderList() {
        Point point = ScreenUtils.getScreenSize(getActivity());
        int width = point.x;
        int height = (int) (point.y * (4.5f / 8.0f));


        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popup_folder_recyclerview, null, false);

        View rview = view.findViewById(R.id.folder_recyclerview);
        // Set the adapter
        if (rview instanceof RecyclerView) {
            Context context = rview.getContext();
            RecyclerView recyclerView = (RecyclerView) rview;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context,LinearLayoutManager.VERTICAL, 0));
            recyclerView.setAdapter(new FolderRecyclerViewAdapter(FolderListContent.ITEMS, mFolderListener));
        }

        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);


        mFolderPopupWindow = new PopupWindow();
        mFolderPopupWindow.setContentView(view);
        mFolderPopupWindow.setWidth(size.x - 50);
        mFolderPopupWindow.setHeight((int)(size.y * 0.5));
        // http://stackoverflow.com/questions/12232724/popupwindow-dismiss-when-clicked-outside
        mFolderPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
        mFolderPopupWindow.setOutsideTouchable(true);
        mFolderPopupWindow.setFocusable(true);
        mFolderPopupWindow.setAnimationStyle(R.style.AnimationPreview);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImageRecyclerViewInteractionListener) {
            mListener = (OnImageRecyclerViewInteractionListener) context;
        } else if(context instanceof  OnFolderRecyclerViewInteractionListener) {
            mFolderListener = (OnFolderRecyclerViewInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFolderListener = null;
    }
}
