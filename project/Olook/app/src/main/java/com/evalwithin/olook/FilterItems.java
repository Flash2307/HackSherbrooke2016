package com.evalwithin.olook;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016-04-23.
 */
public class FilterItems {

    private ArrayList<FilterItem> filterItems;
    private int id;

    public FilterItems()
    {
        this.filterItems = new ArrayList<>();
        this.id = 0;
    }

    public void addNames(ArrayList<String> names)
    {
        for (String name : names)
        {
            addFilter(name);
        }
    }

    public void addFilter(String name)
    {
        this.filterItems.add(new FilterItem(name, true, id++));
    }

    public void changeActive(int id)
    {
        FilterItem filterItem = this.filterItems.get(id);
        filterItem.changeActive();

        this.filterItems.set(id, filterItem);
    }

    public ArrayList<FilterItem> getFilterItems()
    {
        return this.filterItems;
    }

    public class FilterItem {

        private int id;
        private String name;
        private boolean active;

        protected FilterItem(String name, boolean active, int id)
        {
            this.name = name;
            this.active = active;
            this.id = id;
        }

        protected void changeActive()
        {
            this.active = !this.active;
        }

        public String getName()
        {
            return this.name;
        }

        public boolean isActive()
        {
            return this.active;
        }

        public int getId()
        {
            return this.id;
        }
    }
}
