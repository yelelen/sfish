package com.yelelen.sfish.helper;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.yelelen.sfish.Model.DB;
import com.yelelen.sfish.contract.DbDataListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yelelen on 17-9-14.
 */

public abstract class BaseDbHelper<T> {
    public int LAST_INDEX = Contant.MAX_VALUE;
    private static Map<Class<?>, Set<DbDataListener>> mObservers = new HashMap<>();

    protected Set<DbDataListener> getObservers(Class<T> clx) {
        if (mObservers.containsKey(clx)) {
            return mObservers.get(clx);
        }
        return null;
    }
    public void registerObserver(Class<T> clx, DbDataListener<T> observer) {
        Set<DbDataListener> observers = mObservers.get(clx);
        if (observers == null) {
            observers = new HashSet<>();
            mObservers.put(clx, observers);
        }
        observers.add(observer);
    }

    public   void unregisterObserver(Class<T> clx, DbDataListener<T> observer) {
        Set<DbDataListener> observers = mObservers.get(clx);
        if (observers == null)
            return;
        observers.remove(observer);
    }

    protected void notifySave(Class<T> clx, List<T> datas) {
        Set<DbDataListener> observers = mObservers.get(clx);
        if (observers != null && observers.size() > 0)
            for (DbDataListener observer : observers) {
                observer.onSave(datas);
            }
    }

    protected void notifyDelete(Class<T> clx, List<T> datas) {
        Set<DbDataListener> observers = mObservers.get(clx);
        if (observers != null && observers.size() > 0)
            for (DbDataListener observer : observers) {
                observer.onDelete(datas);
            }
    }

     protected void notifyDone(Class<T> clx, List<T> datas) {
        Set<DbDataListener> observers = mObservers.get(clx);
        if (observers != null && observers.size() > 0)
            for (DbDataListener observer : observers) {
                observer.onLocalDone(datas);
            }
    }

    protected void notifyFailed(Class<T> clx, String reason) {
        Set<DbDataListener> observers = mObservers.get(clx);
        if (observers != null && observers.size() > 0)
            for (DbDataListener observer : observers) {
                observer.onLocalFailed(reason);
            }
    }
    public void save(final Class<T> clx, final List<T> datas) {
        if (datas == null || datas.size() == 0)
            return;

        DatabaseDefinition definition = FlowManager.getDatabase(DB.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<T> adapter = FlowManager.getModelAdapter(clx);
                adapter.saveAll(datas);
                notifySave(clx, datas);
            }
        }).build().execute();
    }

    public void delete(final Class<T> clx, final List<T> datas) {
        if (datas == null || datas.size() == 0)
            return;

        DatabaseDefinition definition = FlowManager.getDatabase(DB.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<T> adapter = FlowManager.getModelAdapter(clx);
                adapter.deleteAll(datas);
                notifyDelete(clx, datas);
            }
        }).build().execute();
    }

    public abstract List<T> getFromLocal(int count, boolean order);
    public abstract int getMaxOrder();
}
