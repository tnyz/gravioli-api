package org.gravioli.api;

import org.gravioli.core.Geo;

import java.util.ArrayList;

public class Request {
    String deviceId;
    Boolean metaOnly;
    ArrayList<Geo> locations;
}
