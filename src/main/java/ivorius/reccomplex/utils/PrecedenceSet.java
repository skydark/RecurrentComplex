/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.utils;

import java.util.*;

/**
 * Created by lukas on 11.03.15.
 */
public class PrecedenceSet<T> extends AbstractSet<T> implements Comparable<PrecedenceSet>
{
    protected float precedence;
    protected Set<T> set;

    public PrecedenceSet(float precedence, Set<T> set)
    {
        this.precedence = precedence;
        this.set = set;
    }

    public Set<T> getSet()
    {
        return set;
    }

    public void setSet(Set<T> set)
    {
        this.set = set;
    }

    @Override
    public int compareTo(PrecedenceSet o)
    {
        return Float.compare(precedence, o.precedence);
    }

    @Override
    public Iterator<T> iterator()
    {
        return set.iterator();
    }

    @Override
    public int size()
    {
        return set.size();
    }

    public static interface NativeEntry
    {
        float getPrecedence();
    }
}
