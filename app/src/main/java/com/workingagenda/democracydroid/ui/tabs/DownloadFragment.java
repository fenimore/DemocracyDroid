package com.workingagenda.democracydroid.ui.tabs;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.ui.adapter.DownloadsAdapter;
import com.workingagenda.democracydroid.ui.player.MediaActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadFragment extends Fragment {
    private ListView dList;
    private List<File> files;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_download, container, false);

        files = getListFiles();

        dList = rootView.findViewById(R.id.download_list);
        final TextView dHelp = rootView.findViewById(R.id.download_help);
        dHelp.setText(R.string.download_tab_description);
        dList.setEmptyView(dHelp);
        final Button btnClear = rootView.findViewById(R.id.download_clear);
        final Button btnRefresh = rootView.findViewById(R.id.download_refresh);
        registerForContextMenu(dList);

        dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));

        btnClear.setOnClickListener(v ->
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.delete_all_downloads)
                        .setMessage(R.string.delete_all_downloads_text)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            for (File file : files) {
                                boolean delete = file.delete();
                                Log.d("File: ", file.getName() + delete);
                            }
                            files = getListFiles();
                            dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));

                            Toast.makeText(getActivity(), R.string.downloads_removed, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(R.drawable.ic_warning)
                        .show());

        btnRefresh.setOnClickListener(v -> {
            files = getListFiles();
            dList.setAdapter(
                    new DownloadsAdapter(getContext(), R.layout.row_download, files));
        });

        dList.setOnItemClickListener((parent, view, position, id) -> {
            File f = files.get(position);
            Intent y = new Intent(getContext(), MediaActivity.class);
            y.putExtra("url", Uri.fromFile(f).toString()); //can't pass in article object?
            y.putExtra("title", f.getName());
            startActivity(y);
        });

        return rootView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            MenuInflater inflater = new MenuInflater(getContext());
            menu.setHeaderTitle(R.string.democracy_now);
            inflater.inflate(R.menu.download_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //int pos = ; FIND A WAY TO PASS LIST ITEM POSITION?
        AdapterView.AdapterContextMenuInfo info
                = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null)
            return super.onContextItemSelected(item);
        int pos = info.position;
        File file = files.get(pos);
        switch (item.getItemId()) {
            case R.id.menu_download_delete:
                boolean delete = file.delete();
                Log.d("File: ", file.getName() + delete);
                files = getListFiles();
                dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
                return true;
            case R.id.menu_download_external_player:
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
        Log.d("DownloadFragment", Environment.DIRECTORY_PODCASTS);
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith("dn")
                        || file.getName().endsWith("-podcast.mp4")
                        || file.getName().endsWith("-podcast.mp3")) {// there must be a smarter way to do this
                    if (file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4")) {
                        inFiles.add(file);
                    }
                }
            }
        }
        // Collections.reverse(inFiles);
        return inFiles;
    }
}
