package com.workingagenda.democracydroid.tabfragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.workingagenda.democracydroid.Adapters.DownloadsAdapter;
import com.workingagenda.democracydroid.MediaActivity;
import com.workingagenda.democracydroid.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public TextView Txt1;
    public Button btn;
    public Button btnRefresh;
    public ListView dList;
    public List<File> files;

    public DownloadFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DownloadFragment newInstance(int sectionNumber) {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_download, container, false);

        files = getListFiles();

        dList = rootView.findViewById(android.R.id.list);
        Txt1 = rootView.findViewById(R.id.download_help);
        Txt1.setText("Long Click An Episode to Download, Share, Read Description, and Stream. Long Click a Download to Open it in an External Player.");
        dList.setEmptyView(Txt1);
        btn = rootView.findViewById(R.id.clear);
        btnRefresh = rootView.findViewById(R.id.refresh);
        registerForContextMenu(dList);

        dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle("Delete all downloads")
                        .setMessage("Are you sure you want to delete all episodes?\nLong click and episode to delete them individually.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                for (File file : files) {
                                    boolean delete = file.delete();
                                    Log.d("File: ", file.getName() + delete);
                                }
                                files = getListFiles();
                                dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
                                Toast toast = Toast.makeText(getActivity(), "Downloads Removed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                files = getListFiles();
                dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
            }
        });
        dList.setOnItemClickListener((parent, view, position, id) -> {
            File f = files.get(position);
            Intent y = new Intent(getContext(), MediaActivity.class);
            y.putExtra("url", Uri.fromFile(f).toString()); //can't pass in article object?
            y.putExtra("title", f.getName());
            startActivityForResult(y, 0); //Activity load = 0
        });

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            MenuInflater inflater = new MenuInflater(getContext());
            menu.setHeaderTitle("Democracy Now!");
            inflater.inflate(R.menu.download_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //int pos = ; FIND A WAY TO PASS LiST ITEM POSITION?
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null)
            return super.onContextItemSelected(item);
        int pos = info.position;
        File file = files.get(pos);
        switch (item.getItemId()) {
            case R.id.action_delete:
                boolean delete = file.delete();
                Log.d("File: ", file.getName() + delete);
                files = getListFiles();
                dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
                return true;
            case R.id.action_external_player:
                Intent z = new Intent(Intent.ACTION_VIEW);
                z.setDataAndType(Uri.fromFile(file), "*/*");
                startActivity(z);
            default:
                return super.onContextItemSelected(item);
        }
    }

    private List<File> getListFiles() {
        ArrayList<File> inFiles = new ArrayList<>();
        File parentDir = new File(Environment.getExternalStorageDirectory().toString() +
                File.separator + Environment.DIRECTORY_PODCASTS);
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith("dn") || file.getName().endsWith("-podcast.mp4") || file.getName().endsWith("-podcast.mp3")) { // there must be a smarter way to do this
                    if (file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4")) {
                        inFiles.add(file);
                    }
                }
            }
        }
        // Collections.reverse(inFiles);
        return inFiles;
    }

    private void refresh() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            files = getListFiles();
            dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
        }

    }
}
