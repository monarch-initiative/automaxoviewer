package org.monarchinitiative.automaxoviewer.view;

import org.monarchinitiative.automaxoviewer.model.AutoMaxoRow;
import org.monarchinitiative.automaxoviewer.model.PubMedCitation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PmidAbstractTextVisualizer extends MaxoVisualizer  {


    protected static final String HTML_HEADER = String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <title>Automaxo Annotation</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>%s</style>
            </head>
            <body>
            <div class="limiter">
            <div class="container-table100">
            <div class="wrap-table100">
            <div class="table100 ver1 m-b-110">
            """, CSS);


    private String para(AutoMaxoRow row, int count) {
        return String.format("<h1>Abstract %d/%d</h1>\n", (1+count), row.getCitationList().size());
    }

    private final Set<String> stopWords = Set.of("the", "of", "a", "an", "that");


    private String getMarkedUpText(String text, String hpoLabel, String maxoLabel) {
        StringBuilder sb = new StringBuilder();
        String []words = text.split(("\\s+"));
        Set<String> hpoSet = new HashSet<>();
        Set<String> maxoSet = new HashSet<>();
        String [] hpowords = hpoLabel.split(("\\s+"));
        String [] maxowords = maxoLabel.split(("\\s+"));
        for (var w:hpowords) {
            if (! stopWords.contains(w)) hpoSet.add(w);
        }
        for (var w:maxowords) {
            if (! stopWords.contains(w)) maxoSet.add(w);
        }
        for (var w: words) {
            if (hpoSet.contains(w)) {
                sb.append("<font color=\"#9900FF\"><b>").append(w).append("</b></font> ");
            } else if (maxoSet.contains(w)) {
                sb.append("<font color=\"#0099FF\"><b>").append(w).append("</b></font> ");
            } else {
                sb.append(w).append(" ");
            }
        }
        return sb.toString();
    }


    public String getIntroPara(CurrentItemVisualizable vis) {
        String builder = "<p>" + "Annotation: " + vis.getMondoString() + ": " +
                vis.getMaxoString() + ": " + vis.getHpoString() + "</p>" +
                "<ul style=\"font-size:8px;\">\n" +
                "<li>Total annotations for this disease: " + vis.getTotalAnnots() + "</li>" +
                "<li>Input file: " + vis.getInputFile() + "</li>" +
                "<li>Annotation file: " + vis.getAnnotFile() + "</li>" +
                "</ul>\n";
        return builder;
    }


    public String toHTML(CurrentItemVisualizable vis, int abstractCount) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTML_HEADER);
        builder.append(getIntroPara(vis));
        AutoMaxoRow item = vis.getItem();
        int count = abstractCount;
        List<PubMedCitation> citations = item.getCitationList();
        int N = citations.size();
        count = count % N;
        builder.append(para(item, count));
        PubMedCitation cite = citations.get(count);
        builder.append(getTitle(cite));
        builder.append(getMarkedUpText(cite.getAbstractText(), vis.getHpoString(), vis.getMaxoString()));
        builder.append(HTML_FOOT);
        return builder.toString();
    }

    private String getTitle(PubMedCitation cite) {
        return String.format("<h3>%s (%s)</h3>\n", cite.getTitle(), cite.getPmidTermId().getValue());
    }

    public String toHTML(AutoMaxoRow currentRow, int n) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTML_HEADER);
        List<PubMedCitation> citations = currentRow.getCitationList();
        int N = citations.size();
        int count = n % N;
        builder.append(para(currentRow, count));
        PubMedCitation cite = citations.get(count);
        builder.append(cite.getAbstractText());
        builder.append(HTML_FOOT);
        return builder.toString();
    }

    public String getHtmlNoAbstract() {
        StringBuilder builder = new StringBuilder();
        builder.append(HTML_HEADER);
        builder.append("<h2>Error</h2>");
        builder.append("<p>No row chosen. Open an automax file and choose a row</p>");
        builder.append(HTML_FOOT);
        return builder.toString();
    }
}
