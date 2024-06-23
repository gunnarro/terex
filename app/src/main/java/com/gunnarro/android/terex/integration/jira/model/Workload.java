package com.gunnarro.android.terex.integration.jira.model;

import java.util.ArrayList;

public class Workload {
     Metadata metadata;
     ArrayList<Result> results;
     String self;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }
}
