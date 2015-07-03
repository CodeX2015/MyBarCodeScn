package com.android.app.mybarcodescn.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.app.mybarcodescn.ProductDetails;
import com.android.app.mybarcodescn.R;
import com.android.app.mybarcodescn.Stock;
import com.android.app.mybarcodescn.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


/**
 * Created by CodeX on 02.07.2015.
 */

public class StickyListHeaderAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {
    private Set<Stock> mStocksSet;
    private ArrayList<Stock> mStocks;
    private int[] mSectionIndices;
    private String[] mSectionNames;
    private LayoutInflater mInflater;
    LinkedHashMap<String, String> mSections;


    public Set<Stock> convertArrayList2HashSet(ArrayList<Stock> stocks) {
        Set<Stock> setOfStocks = new HashSet<Stock>(stocks);
        return setOfStocks;
    }

    public StickyListHeaderAdapter(Context context, ArrayList<Stock> stocks) {
        mInflater = LayoutInflater.from(context);
        mStocks = stocks;
        StockComparator stockComparator = new StockComparator();
        Collections.sort(stocks, stockComparator);
        mSections = findSections();
        Utils.setmHeadersCount(mSections.size());
        mSectionIndices = getSectionIndices();
        mSectionNames = getSectionNames();
        mStocksSet = convertArrayList2HashSet(stocks);
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        String lastFirstStock = mStocks.get(0).getName();
        sectionIndices.add(0);
        for (int i = 1; i < mStocks.size(); i++) {
            if (mStocks.get(i).getName() != lastFirstStock) {
                lastFirstStock = mStocks.get(i).getName();
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        String[] sectionsStr = mSections.values().toArray(new String[mSections.size() - 1]);
        int[] numbers = new int[sectionsStr.length];
        for(int i = 0;i < sectionsStr.length;i++)
        {
            // Note that this is assuming valid input
            // If you want to check then add a try/catch
            // and another index for the numbers if to continue adding the others
            numbers[i] = Integer.parseInt(sectionsStr[i]);
        }
        return numbers;
    }


    private LinkedHashMap<String, String> findSections() {
        mSections = new LinkedHashMap<String, String>();
        int n = mStocks.size();
        int nSections = 0;
        for (int i = 0; i < n; i++) {
            String sectionName = mStocks.get(i).getName();

            if (!mSections.containsKey(sectionName)) {
                mSections.put(sectionName, String.valueOf(nSections));
                nSections++;
            }
        }

        //return mSections.keySet().toArray(new String[mSections.size()]);
        return mSections;
    }

    private String[] getSectionNames() {
        return mSections.keySet().toArray(new String[mSections.size()-1]);
    }

    @Override
    public int getCount() {
        return mStocks.size();
    }

    @Override
    public Stock getItem(int position) {
        return mStocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Todo adapter parameter HashSet() stocks
        MyRow myRow;
        if (convertView == null) {
            myRow = new MyRow();
            convertView = mInflater.inflate(R.layout.row_list_item, parent, false);
            myRow.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
            myRow.tvStock = (TextView) convertView.findViewById(R.id.tvStock);
            convertView.setTag(myRow);
        } else {
            myRow = (MyRow) convertView.getTag();
        }
        myRow.tvSize.setText("Size: " + String.valueOf(getItem(position).getProduct().get(0).getSize()));
        myRow.tvStock.setText("Stock: " + String.valueOf(getItem(position).getName()));

        return convertView;
    }

    private class MyRow {
        private TextView tvSize;
        private TextView tvStock;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.row_list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.list_header_title);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        Log.d("MYDEBUG", "Stock -" + getItem(position).getName() + " position - " + position);
        String headerString = mStocks.get(position).getName();
        holder.text.setText(headerString);

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return mStocks.get(position).getName().charAt(0);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionNames;
    }

    class HeaderViewHolder {
        TextView text;
    }

    private class StockComparator implements Comparator<Stock> {
        @Override
        public int compare(Stock stock1, Stock stock2) {
            return stock1.getName().compareTo(stock2.getName());
        }
    }

}

