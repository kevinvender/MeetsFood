package com.sidera.meetsfood.api.beans;

/**
 * Created by kevin.vender on 20/09/2017.
 * Vers 2.0
 */

import java.sql.ResultSet;
import java.sql.SQLException;


public class Tariffe {

    public String codice = "";
    public String info = "";
    public String tipologia = "";

    public Tariffe(ResultSet resultSet) throws SQLException {
        codice = resultSet.getString("TARIFFA");
        info = resultSet.getString("DENO");
        tipologia = resultSet.getString("TIPOLOGIA");
    }
}
