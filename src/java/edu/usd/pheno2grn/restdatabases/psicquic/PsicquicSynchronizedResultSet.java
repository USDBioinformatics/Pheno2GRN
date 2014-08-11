package edu.usd.pheno2grn.restdatabases.psicquic;

import java.util.HashSet;

/**
 * Allows multiple threads to add results to the resultSet when the threads have
 * the lock.
 */
public class PsicquicSynchronizedResultSet {

    public HashSet<PsicquicResult> resultSet = new HashSet<>();
    final public Object lock = new Object();
}
