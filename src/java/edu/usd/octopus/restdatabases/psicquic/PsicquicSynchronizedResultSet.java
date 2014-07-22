/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.octopus.restdatabases.psicquic;

import java.util.HashSet;

/**
 *
 * @author Nick.Weinandt
 */
public class PsicquicSynchronizedResultSet{
    public HashSet<PsicquicResult> resultSet=new HashSet<>();
    final public Object lock=new Object();

}
