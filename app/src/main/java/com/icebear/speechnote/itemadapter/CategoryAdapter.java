package com.icebear.speechnote.itemadapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.activity.CategoryDetails;
import com.icebear.speechnote.activity.MainActivity;
import com.icebear.speechnote.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> implements View.OnClickListener {


    private ArrayList<Category> arrayList;

    private MainActivity activity;
    private Context context;


    public CategoryAdapter(MainActivity activity, Context context, ArrayList<Category> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.context = context;


//        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView category;
        public TextView notecount;
        public ImageView more;
        LinearLayout itemNote;

        public MyViewHolder(View itemView) {
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.title);
            more = (ImageView) itemView.findViewById(R.id.more);
            itemNote = (LinearLayout) itemView.findViewById(R.id.item_note);
            notecount = (TextView) itemView.findViewById(R.id.notecount);

        }


    }


    public CategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        return new MyViewHolder(itemView);
    }


    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Category category = arrayList.get(position);
        holder.category.setText(category.getCategory());
        holder.notecount.setText(category.getNotecount() + " notes");
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopupMenu(holder.more, category);
            }
        });
        holder.itemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(category);
            }
        });


    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    private void openCategory(Category category) {
        Intent intent = new Intent(activity, CategoryDetails.class);
        intent.putExtra(NoteConst.OBJECT, category);
        activity.startActivity(intent);
    }


    public Category getItem(int position) {
        return arrayList.get(position);
    }


    private void openPopupMenu(ImageView more, final Category category) {
        PopupMenu popup = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(context, more, Gravity.BOTTOM);
        } else {
            popup = new PopupMenu(context, more);
        }
//Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_category, popup.getMenu());

//registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    editCategory(category);
                } else if (id == R.id.action_delete) {
                    confirmDelete(category);
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private void editCategory(final Category category) {
        final Dialog dlg = new Dialog(activity);
        Log.i("quanpna", "step2: show file name diaglog ");
        dlg.requestWindowFeature(1);
        dlg.setContentView(R.layout.dialog_add_new_category);
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        final Button cancel = (Button) dlg.findViewById(R.id.cancel);
        final EditText fileNameEtx = (EditText) dlg.findViewById(R.id.edit_name);
        fileNameEtx.setText(category.getCategory());
        Button ok = (Button) dlg.findViewById(R.id.cofirm);

        dlg.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fileNameEtx.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(activity, R.string.filename_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("aaaa", category.getId() + " cateId: ");
                category.setCategory(fileNameEtx.getText().toString());
                activity.database.updateCategory(category);
                dlg.dismiss();
                Intent intent = new Intent(NoteConst.UPDATE_CATEGORY);
                activity.sendBroadcast(intent);
            }
        });
    }

    private void confirmDelete(final Category category) {
        CharSequence message;
        CharSequence title;

        Log.i("aaaa", category.getId() + " cateId: ");
        title = context.getResources().getText(R.string.delete_note_confirm);
        new AlertDialog.Builder(activity).
                setTitle(title).
//                setMessage(message).
                setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        onDelete(category);
    }
}).
                setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).
                setCancelable(true).
                show();


    }

    private void onDelete(Category category) {
        Intent intent = new Intent(NoteConst.DELETE_CATEGORY);
        intent.putExtra(NoteConst.OBJECT, category);
        activity.sendBroadcast(intent);
    }

}
