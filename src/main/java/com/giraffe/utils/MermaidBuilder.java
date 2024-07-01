package com.giraffe.utils;

import java.util.ArrayList;
import java.util.List;

public class MermaidBuilder {
    private final List<String> nodes;
    private final List<String> links;

    public MermaidBuilder() {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
    }

    public MermaidBuilder addNode(String node) {
        nodes.add(node);
        return this;
    }

    public MermaidBuilder addLink(String from, String to) {
        links.add(from + " --> " + to);
        return this;
    }

    public MermaidBuilder addDottedLink(String from, String to) {
        links.add(from + " -.-> " + to);
        return this;
    }

    public MermaidBuilder addDottedLink(String from, String to, String explain) {
        links.add(from + " -.-|" + explain + "| "  + to);
        return this;
    }

    public MermaidBuilder addThickLink(String from, String to) {
        links.add(from + " ==> " + to);
        return this;
    }

    public MermaidBuilder addForkLink(String from, String to, String explain) {
        links.add(from + " --x|" + explain + "| " + to);
        return this;
    }

    public String buildLR() {
        StringBuilder sb = new StringBuilder();
        sb.append("graph LR\n");
        for (String link : links) {
            sb.append("    ").append(link).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        MermaidBuilder builder = new MermaidBuilder();
        builder.addNode("Start")
                .addNode("Step1")
                .addNode("Step2")
                .addNode("End")
                .addLink("Start", "Step1")
                .addLink("Step1", "Step2")
                .addLink("Step2", "End");

        System.out.println(builder.buildLR());
    }
}
