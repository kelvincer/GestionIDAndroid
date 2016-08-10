package com.eeec.GestionEspresso.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eeec.GestionEspresso.R;
import com.eeec.GestionEspresso.manager.GestionRequestManager;
import com.eeec.GestionEspresso.model.KArticle;
import com.eeec.GestionEspresso.model.KCategory;
import com.eeec.GestionEspresso.model.KEdition;

public class EditionAdapter extends BaseExpandableListAdapter
{
    private Context context;

    public KEdition getEdition()
    {
        return edition;
    }

    public void setEdition(KEdition edition)
    {
        this.edition = edition;
    }

    private KEdition edition;

    public EditionAdapter(Context context)
    {
        this.context = context;
        this.edition = null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon)
    {
        return this.edition.categories.get(groupPosition).articles.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final KArticle article = (KArticle) getChild(groupPosition, childPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.article_row, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.title);
        txtListChild.setText(article.title);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        if (this.edition == null)
        {
            return 0;
        }
        else
        {
            return this.edition.categories.get(groupPosition).articles.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.edition.categories.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        if (this.edition == null)
        {
            return 0;
        }
        else
        {
            return this.edition.categories.size();
        }
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        KCategory category = (KCategory) getGroup(groupPosition);
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.category_row, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.name);
        lblListHeader.setText(category.name);

        NetworkImageView image = (NetworkImageView) convertView.findViewById(R.id.categoryImage);
        ImageLoader imageLoader = GestionRequestManager.getInstance(this.context).getImageLoader();
        image.setImageUrl(category.icon, imageLoader);
        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}