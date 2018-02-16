package com.repina.anastasia.momandbaby.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid item delete adapter
 */
public class GridItemArrayAdapter extends ArrayAdapter<GridItem> {

    private final int invalid = -1;
    private int delete_pos = -1;
    private List<GridItem> itemList = new ArrayList<>();

    public GridItemArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public void clear() {
        itemList.clear();
    }

    @Override
    public void add(GridItem object) {
        itemList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.itemList.size();
    }

    @Override
    public GridItem getItem(int index) {
        return this.itemList.get(index);
    }

    /**
     * Item was swiped
     *
     * @param isRight  is right swipe
     * @param position delete index
     */
    public void onSwipeItem(boolean isRight, int position) {
        if (!isRight) {
            delete_pos = position;
        } else if (delete_pos == position) {
            delete_pos = invalid;
        }
        notifyDataSetChanged();
    }

    /**
     * Item delete
     *
     * @param pos delete index
     */
    private void deleteItem(int pos) {
        GridItem removing = itemList.get(pos);
        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();
        final DatabaseReference databaseReference = database.getReference(removing.getType());
        databaseReference.child(removing.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            child.getRef().setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        itemList.remove(pos);
        if (itemList.size() == 0) {
            GridItem it = new GridItem(R.mipmap.cross, "R.mipmap.cross", getContext().getString(R.string.no_data_today), null, null);
            itemList.add(it);
        }
        delete_pos = invalid;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row, parent, false);
        }
        ImageView itemImg = ViewHolderPattern.get(convertView, R.id.itemImg);
        TextView itemDesc = ViewHolderPattern.get(convertView, R.id.itemDesc);
        Button delete = ViewHolderPattern.get(convertView, R.id.delete);
        if (delete_pos == position) {
            delete.setVisibility(View.VISIBLE);
        } else
            delete.setVisibility(View.GONE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
            }
        });
        GridItem item = getItem(position);
        itemImg.setImageResource(item.getItemImg());
        itemDesc.setText(item.getItemDesc());
        return convertView;
    }
}

/**
 * View holder
 */
class ViewHolderPattern {
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}

