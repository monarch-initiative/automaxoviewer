package org.monarchinitiative.automaxoviewer.view;

public class MaxoVisualizer {

    protected static final String CSS = """
                        
            /*//////////////////////////////////////////////////////////////////
         
            /*//////////////////////////////////////////////////////////////////
            [ RESTYLE TAG ]*/
            body, html {
            height: 100%;
            font-family: sans-serif;
            }
            /* ------------------------------------ */
            a {
            margin: 0px;
            transition: all 0.4s;
            -webkit-transition: all 0.4s;
            -o-transition: all 0.4s;
            -moz-transition: all 0.4s;
            }
            a:focus {
            outline: none !important;
            }
            a:hover {
            text-decoration: none;
            }
            /* ------------------------------------ */
            h1,h2,h3,h4,h5,h6 {margin: 0px;}
            p {margin: 0px;}
            ul, li {
            margin: 0px;
            list-style-type: none;
            }
            /* ------------------------------------ */
            input {
            display: block;
            outline: none;
            border: none;
            }
            textarea {
            display: block;
            outline: none;
            }
            textarea:focus, input:focus {
            border-color: transparent;
            }
            /* ------------------------------------ */
            button {
            outline: none;
            border: none;
            background: transparent;
            }
            button:hover {
            cursor: pointer;
            }
            iframe {
            border: none;
            }
            /*//////////////////////////////////////////////////////////////////
            [ Table ]*/
            .limiter {
            width: 100%;
            margin: 0 auto;
            }
            .container-table100 {
            width: 100%;
            min-height: 100vh;
            background: white;
            display: -webkit-box;
            display: -webkit-flex;
            display: -moz-box;
            display: -ms-flexbox;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-wrap: wrap;
            padding: 3px 3px;
            }
            .wrap-table100 {
            width: 1300px;
            }
            /*//////////////////////////////////////////////////////////////////
            [ Table ]*/
            table {
            width: 100%;
            background-color: #fff;
            }
            th, td {
            font-weight: unset;
            padding-right: 10px;
            }
            .column100 {
            width: 130px;
            padding-left: 5px;
            }
            .column100.column1 {
               width: 90px;
               padding-left: 5px;
               }
            .column100.column2 {
               width: 500px;
               padding-left: 5px;
            }
            .row100.head th {
            padding-top: 5px;
            padding-bottom: 5px;
            }
            .row100 td {
            padding-top: 5px;
            padding-bottom: 5px;
            }
            /*==================================================================
            [ Ver1 ]*/
            .table100.ver1 td {
            font-family: Montserrat-Regular;
            font-size: 14px;
            color: #808080;
            line-height: 1.4;
            }
            .table100.ver1 th {
            font-family: Montserrat-Medium;
            font-size: 12px;
            color: #fff;
            line-height: 1.4;
            text-transform: uppercase;
            background-color: #36304a;
            }
            .table100.ver1 .row100:hover {
            background-color: #f2f2f2;
            }
            .table100.ver1 .hov-column-ver1 {
            background-color: #f2f2f2;
            }
            .table100.ver1 .hov-column-head-ver1 {
            background-color: #484848;
            }
            .table100.ver1 .row100 td:hover {
            background-color: #6c7ae0;
            color: #fff;
            }
            /*==================================================================
            [ Ver2 ]*/
            .table100.ver2 td {
            font-family: Montserrat-Regular;
            font-size: 14px;
            color: #808080;
            line-height: 1.4;
            }
            .table100.ver2 th {
            font-family: Montserrat-Medium;
            font-size: 12px;
            color: #fff;
            line-height: 1.4;
            text-transform: uppercase;
            background-color: #333333;
            }
            .table100.ver2 .row100:hover td {
            background-color: #83d160;
            color: #fff;
            }
            .table100.ver2 .hov-column-ver2 {
            background-color: #83d160;
            color: #fff;
            }
            .table100.ver2 .hov-column-head-ver2 {
            background-color: #484848;
            }
            .table100.ver2 .row100 td:hover {
            background-color: #57b846;
            color: #fff;
            }
            /*==================================================================
            [ Ver2 ]*/
            .table100.ver2 tbody tr:nth-child(even) {
            background-color: #eaf8e6;
            }
            .table100.ver2 td {
            font-family: Montserrat-Regular;
            font-size: 14px;
            color: #808080;
            line-height: 1.4;
            }
            .table100.ver2 th {
            font-family: Montserrat-Medium;
            font-size: 12px;
            color: #fff;
            line-height: 1.4;
            text-transform: uppercase;
            background-color: #333333;
            }
            .table100.ver2 .row100:hover td {
            background-color: #83d160;
            color: #fff;
            }
            .table100.ver2 .hov-column-ver2 {
            background-color: #83d160;
            color: #fff;
            }
            .table100.ver2 .hov-column-head-ver2 {
            background-color: #484848;
            }
            .table100.ver2 .row100 td:hover {
            background-color: #57b846;
            color: #fff;
            }
            /*==================================================================
            [ Ver3 ]*/
            .table100.ver3 tbody tr {
            border-bottom: 1px solid #e5e5e5;
            }
            .table100.ver3 td {
            font-family: Montserrat-Regular;
            font-size: 14px;
            color: #808080;
            line-height: 1.4;
            }
            .table100.ver3 th {
            font-family: Montserrat-Medium;
            font-size: 12px;
            color: #fff;
            line-height: 1.4;
            text-transform: uppercase;
            background-color: #6c7ae0;
            }
            .table100.ver3 .row100:hover td {
            background-color: #fcebf5;
            }
            .table100.ver3 .hov-column-ver3 {
            background-color: #fcebf5;
            }
            .table100.ver3 .hov-column-head-ver3 {
            background-color: #7b88e3;
            }
            .table100.ver3 .row100 td:hover {
            background-color: #e03e9c;
            color: #fff;
            }
            /*==================================================================
            [ Ver4 ]*/
            .table100.ver4 td {
            font-family: Montserrat-Regular;
            font-size: 14px;
            color: #808080;
            line-height: 1.4;
            }
            .table100.ver4 th {
            font-family: Montserrat-Medium;
            font-size: 12px;
            color: #fff;
            line-height: 1.4;
            text-transform: uppercase;
            background-color: #fa4251;
            }
            .table100.ver4 .row100:hover td {
            color: #fa4251;
            }
            .table100.ver4 .hov-column-ver4 {
            background-color: #ffebed;
            }
            .table100.ver4 .hov-column-head-ver4 {
            background-color: #f95462;
            }
            .table100.ver4 .row100 td:hover {
            background-color: #ffebed;
            color: #fa4251;
            }
            /*==================================================================
            [ Ver5 ]*/
            .table100.ver5 tbody tr:nth-child(even) {
            background-color: #e9faff;
            }
            .table100.ver5 td {
            font-family: Montserrat-Regular;
            font-size: 14px;
            color: #808080;
            line-height: 1.4;
            position: relative;
            }
            .table100.ver5 th {
            font-family: Montserrat-Medium;
            font-size: 12px;
            color: #fff;
            line-height: 1.4;
            text-transform: uppercase;
            background-color: #002933;
            }
            .table100.ver5 .row100:hover td {
            color: #fe3e64;
            }
            .table100.ver5 .hov-column-ver5 {color: #fe3e64;}
            .table100.ver5 .hov-column-ver5::before {
            content: "";
            display: block;
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            pointer-events: none;
            border-left: 1px solid #f2f2f2;
            border-right: 1px solid #f2f2f2;
            }
            .table100.ver5 .hov-column-head-ver5 {
            background-color: #1a3f48;
            color: #fe3e64;
            }
            .table100.ver5 .row100 td:hover {
            color: #fe3e64;
            }
            .table100.ver5 .row100 td:hover:before {
            content: "";
            display: block;
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            pointer-events: none;
            border: 1px solid #fe3e64;
            }
            /*==================================================================
            [ Ver6 ]*/
            .table100.ver6 {
            border-radius: 16px;
            overflow: hidden;
            background: #7918f2;
            background: -webkit-linear-gradient(-68deg, #ac32e4 , #4801ff);
            background: -o-linear-gradient(-68deg, #ac32e4 , #4801ff);
            background: -moz-linear-gradient(-68deg, #ac32e4 , #4801ff);
            background: linear-gradient(-68deg, #ac32e4 , #4801ff);
            }
            .table100.ver6 table {
            background-color: transparent;
            }
            .table100.ver6 td {
            font-family: Montserrat-Regular;
            font-size: 14px;
            color: #fff;
            line-height: 1.4;
            }
            .table100.ver6 th {
            font-family: Montserrat-Medium;
            font-size: 12px;
            color: #fff;
            line-height: 1.4;
            text-transform: uppercase;
            background-color: rgba(255,255,255,0.32);
            }
            .table100.ver6 .row100:hover td {
            background-color: rgba(255,255,255,0.1);
            }
            .table100.ver6 .hov-column-ver6 {
            background-color: rgba(255,255,255,0.1);
            }
            .table100.ver6 .row100 td:hover {
            background-color: rgba(255,255,255,0.2);
            }
            """;


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
            <table data-vertable="ver1">
            <thead>
            <tr class="row100 head">
            <th class="column100 column1" data-column="Item">Item</th>
            <th class="column100 column2" data-column="Value">Value</th>
            </tr>
            </thead>
            <tbody>
            """, CSS);


    protected final static String HTML_FOOT = """
            </tbody>
            </table>
            </div>         
            </div>
            </div>
            </div>
            </body>
            </html>\s
            """;

}
