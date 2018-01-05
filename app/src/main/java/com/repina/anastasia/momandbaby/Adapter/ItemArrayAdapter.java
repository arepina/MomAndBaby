package com.repina.anastasia.momandbaby.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.List;

public class ItemArrayAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList = new ArrayList<>();

    private static class ItemViewHolder {
        ImageView itemImg;
        TextView itemDesc;//todo add the whole desc of feature using overriden to string methods
    }

    public ItemArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public void clear() {
        itemList.clear();
    }

    @Override
    public void add(Item object) {
        itemList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.itemList.size();
    }

    @Override
    public Item getItem(int index) {
        return this.itemList.get(index);
    }

    public boolean hasItem(Item item)
    {
        for(Item i : itemList)
            if(i.getItemDesc().equals(item.getItemDesc()) && i.getItemImg() == item.getItemImg())
                return true;
        return false;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_row, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.itemImg = (ImageView) row.findViewById(R.id.itemImg);
            viewHolder.itemDesc = (TextView) row.findViewById(R.id.itemDesc);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)row.getTag();
        }
        Item item = getItem(position);
        viewHolder.itemImg.setImageResource(item.getItemImg());
        viewHolder.itemDesc.setText(item.getItemDesc());
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

