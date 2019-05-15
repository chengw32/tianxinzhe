package com.plugin.tianxingzhex.hook.wechat;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlToJson {
    private static final String DEFAULT_CONTENT_NAME = "content";
    private static final boolean DEFAULT_EMPTY_BOOLEAN = false;
    private static final double DEFAULT_EMPTY_DOUBLE = 0.0d;
    private static final int DEFAULT_EMPTY_INTEGER = 0;
    private static final long DEFAULT_EMPTY_LONG = 0;
    private static final String DEFAULT_EMPTY_STRING = "";
    private static final String DEFAULT_ENCODING = "utf-8";
    private static final String DEFAULT_INDENTATION = "   ";
    private static final String TAG = "XmlToJson";
    private HashMap<String, String> mAttributeNameReplacements;
    private HashMap<String, String> mContentNameReplacements;
    private HashMap<String, Class<?>> mForceClassForPath;
    private HashSet<String> mForceListPaths;
    private String mIndentationPattern;
    private String mInputEncoding;
    private InputStream mInputStreamSource;
    private JSONObject mJsonObject;
    private HashSet<String> mSkippedAttributes;
    private HashSet<String> mSkippedTags;
    private StringReader mStringSource;

    public static class Builder {
        private HashMap<String, String> mAttributeNameReplacements = new HashMap();
        private HashMap<String, String> mContentNameReplacements = new HashMap();
        private HashMap<String, Class<?>> mForceClassForPath = new HashMap();
        private HashSet<String> mForceListPaths = new HashSet();
        private String mInputEncoding = XmlToJson.DEFAULT_ENCODING;
        private HashSet<String> mSkippedAttributes = new HashSet();
        private HashSet<String> mSkippedTags = new HashSet();
        private StringReader mStringSource;

        public Builder(String str) {
            this.mStringSource = new StringReader(str);
        }

        public JSONObject build() {
            try {
                return new JSONObject(new XmlToJson(this).toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /*  JADX ERROR: NullPointerException in pass: BlockSplitter
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.blocksmaker.BlockSplitter.connectExceptionHandlers(BlockSplitter.java:243)
        	at jadx.core.dex.visitors.blocksmaker.BlockSplitter.setupConnections(BlockSplitter.java:226)
        	at jadx.core.dex.visitors.blocksmaker.BlockSplitter.splitBasicBlocks(BlockSplitter.java:116)
        	at jadx.core.dex.visitors.blocksmaker.BlockSplitter.visit(BlockSplitter.java:46)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JavaClass.getCode(JavaClass.java:48)
        */
    private void putContent(java.lang.String r3, org.json.JSONObject r4, java.lang.String r5, java.lang.String r6) {
        /*
        r2 = this;
        r0 = r2.mForceClassForPath;	 Catch:{ JSONException -> 0x007a }
        r3 = r0.get(r3);	 Catch:{ JSONException -> 0x007a }
        r3 = (java.lang.Class) r3;	 Catch:{ JSONException -> 0x007a }
        if (r3 != 0) goto L_0x0013;	 Catch:{ JSONException -> 0x007a }
    L_0x000a:
        if (r6 != 0) goto L_0x000e;	 Catch:{ JSONException -> 0x007a }
    L_0x000c:
        r6 = "";	 Catch:{ JSONException -> 0x007a }
    L_0x000e:
        r4.put(r5, r6);	 Catch:{ JSONException -> 0x007a }
        goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x0013:
        r0 = java.lang.Integer.class;	 Catch:{ JSONException -> 0x007a }
        r1 = 0;
        if (r3 != r0) goto L_0x0028;
    L_0x0018:
        r3 = java.lang.Integer.parseInt(r6);	 Catch:{ NumberFormatException -> 0x0024 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ NumberFormatException -> 0x0024 }
        r4.put(r5, r3);	 Catch:{ NumberFormatException -> 0x0024 }
        goto L_0x007a;
    L_0x0024:
        r4.put(r5, r1);	 Catch:{ JSONException -> 0x007a }
        goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x0028:
        r0 = java.lang.Long.class;	 Catch:{ JSONException -> 0x007a }
        if (r3 != r0) goto L_0x003e;
    L_0x002c:
        r0 = java.lang.Long.parseLong(r6);	 Catch:{ NumberFormatException -> 0x0038 }
        r3 = java.lang.Long.valueOf(r0);	 Catch:{ NumberFormatException -> 0x0038 }
        r4.put(r5, r3);	 Catch:{ NumberFormatException -> 0x0038 }
        goto L_0x007a;
        r0 = 0;
        r4.put(r5, r0);	 Catch:{ JSONException -> 0x007a }
        goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x003e:
        r0 = java.lang.Double.class;	 Catch:{ JSONException -> 0x007a }
        if (r3 != r0) goto L_0x0054;
    L_0x0042:
        r0 = java.lang.Double.parseDouble(r6);	 Catch:{ NumberFormatException -> 0x004e }
        r3 = java.lang.Double.valueOf(r0);	 Catch:{ NumberFormatException -> 0x004e }
        r4.put(r5, r3);	 Catch:{ NumberFormatException -> 0x004e }
        goto L_0x007a;
        r0 = 0;
        r4.put(r5, r0);	 Catch:{ JSONException -> 0x007a }
        goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x0054:
        r0 = java.lang.Boolean.class;	 Catch:{ JSONException -> 0x007a }
        if (r3 != r0) goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x0058:
        if (r6 != 0) goto L_0x005e;	 Catch:{ JSONException -> 0x007a }
    L_0x005a:
        r4.put(r5, r1);	 Catch:{ JSONException -> 0x007a }
        goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x005e:
        r3 = "true";	 Catch:{ JSONException -> 0x007a }
        r3 = r6.equalsIgnoreCase(r3);	 Catch:{ JSONException -> 0x007a }
        if (r3 == 0) goto L_0x006b;	 Catch:{ JSONException -> 0x007a }
    L_0x0066:
        r3 = 1;	 Catch:{ JSONException -> 0x007a }
        r4.put(r5, r3);	 Catch:{ JSONException -> 0x007a }
        goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x006b:
        r3 = "false";	 Catch:{ JSONException -> 0x007a }
        r3 = r6.equalsIgnoreCase(r3);	 Catch:{ JSONException -> 0x007a }
        if (r3 == 0) goto L_0x0077;	 Catch:{ JSONException -> 0x007a }
    L_0x0073:
        r4.put(r5, r1);	 Catch:{ JSONException -> 0x007a }
        goto L_0x007a;	 Catch:{ JSONException -> 0x007a }
    L_0x0077:
        r4.put(r5, r1);	 Catch:{ JSONException -> 0x007a }
    L_0x007a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ds.xpay.utils.XmlToJson.putContent(java.lang.String, org.json.JSONObject, java.lang.String, java.lang.String):void");
    }

    private XmlToJson(Builder builder) {
        this.mIndentationPattern = DEFAULT_INDENTATION;
        this.mSkippedAttributes = new HashSet();
        this.mSkippedTags = new HashSet();
        this.mStringSource = builder.mStringSource;
        this.mInputEncoding = builder.mInputEncoding;
        this.mForceListPaths = builder.mForceListPaths;
        this.mAttributeNameReplacements = builder.mAttributeNameReplacements;
        this.mContentNameReplacements = builder.mContentNameReplacements;
        this.mForceClassForPath = builder.mForceClassForPath;
        this.mSkippedAttributes = builder.mSkippedAttributes;
        this.mSkippedTags = builder.mSkippedTags;
        this.mJsonObject = convertToJSONObject();
    }

    /* JADX WARNING: Removed duplicated region for block: B:6:0x002e A:{ExcHandler: org.xmlpull.v1.XmlPullParserException (r0_2 'e' org.xmlpull.v1.XmlPullParserException), Splitter: B:0:0x0000} */
    /* JADX WARNING: Missing block: B:6:0x002e, code:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:7:0x002f, code:
            r0.printStackTrace();
     */
    /* JADX WARNING: Missing block: B:8:0x0033, code:
            return null;
     */
    private org.json.JSONObject convertToJSONObject() {
        /*
        r4 = this;
        r0 = new com.ds.xpay.utils.Tag;	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r1 = "";
        r2 = "xml";
        r0.<init>(r1, r2);	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r1 = org.xmlpull.v1.XmlPullParserFactory.newInstance();	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r2 = 0;
        r1.setNamespaceAware(r2);	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r1 = r1.newPullParser();	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r4.setInput(r1);	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r3 = r1.getEventType();	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
    L_0x001c:
        if (r3 == 0) goto L_0x0023;
    L_0x001e:
        r3 = r1.next();	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        goto L_0x001c;
    L_0x0023:
        r4.readTags(r0, r1);	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r4.unsetInput();	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        r0 = r4.convertTagToJson(r0, r2);	 Catch:{ XmlPullParserException -> 0x002e, XmlPullParserException -> 0x002e }
        return r0;
    L_0x002e:
        r0 = move-exception;
        r0.printStackTrace();
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ds.xpay.utils.XmlToJson.convertToJSONObject():org.json.JSONObject");
    }

    private void setInput(XmlPullParser xmlPullParser) {
        if (this.mStringSource != null) {
            try {
                xmlPullParser.setInput(this.mStringSource);
                return;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            xmlPullParser.setInput(this.mInputStreamSource, this.mInputEncoding);
        } catch (XmlPullParserException e2) {
            e2.printStackTrace();
        }
    }

    private void unsetInput() {
        if (this.mStringSource != null) {
            this.mStringSource.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ae A:{ExcHandler: org.xmlpull.v1.XmlPullParserException (r9_1 'e' org.xmlpull.v1.XmlPullParserException), Splitter: B:0:0x0000} */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ae A:{ExcHandler: org.xmlpull.v1.XmlPullParserException (r9_1 'e' org.xmlpull.v1.XmlPullParserException), Splitter: B:0:0x0000} */
    /* JADX WARNING: Missing block: B:24:0x00ae, code:
            r9 = move-exception;
     */
    /* JADX WARNING: Missing block: B:25:0x00af, code:
            r9.printStackTrace();
     */



    private String getAttributeNameReplacement(String str, String str2) {
        str = (String) this.mAttributeNameReplacements.get(str);
        return str != null ? str : str2;
    }

    private String getContentNameReplacement(String str, String str2) {
        str = (String) this.mContentNameReplacements.get(str);
        return str != null ? str : str2;
    }

    public String toString() {
        return this.mJsonObject != null ? this.mJsonObject.toString() : null;
    }

    public String toFormattedString() {
        if (this.mJsonObject == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        format(this.mJsonObject, stringBuilder, "");
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private void format(JSONObject jSONObject, StringBuilder stringBuilder, String str) {
        Iterator keys = jSONObject.keys();
        while (keys.hasNext()) {
            String str2 = (String) keys.next();
            stringBuilder.append(str);
            stringBuilder.append(this.mIndentationPattern);
            stringBuilder.append("\"");
            stringBuilder.append(str2);
            stringBuilder.append("\": ");
            Object opt = jSONObject.opt(str2);
            StringBuilder stringBuilder2;
            if (opt instanceof JSONObject) {
                JSONObject jSONObject2 = (JSONObject) opt;
                stringBuilder.append(str);
                stringBuilder.append("{\n");
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(this.mIndentationPattern);
                format(jSONObject2, stringBuilder, stringBuilder2.toString());
                stringBuilder.append(str);
                stringBuilder.append(this.mIndentationPattern);
                stringBuilder.append("}");
            } else if (opt instanceof JSONArray) {
                JSONArray jSONArray = (JSONArray) opt;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(this.mIndentationPattern);
                formatArray(jSONArray, stringBuilder, stringBuilder2.toString());
            } else {
                formatValue(opt, stringBuilder);
            }
            if (keys.hasNext()) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n");
            }
        }
    }

    private void formatArray(JSONArray jSONArray, StringBuilder stringBuilder, String str) {
        stringBuilder.append("[\n");
        for (int i = 0; i < jSONArray.length(); i++) {
            Object opt = jSONArray.opt(i);
            StringBuilder stringBuilder2;
            if (opt instanceof JSONObject) {
                JSONObject jSONObject = (JSONObject) opt;
                stringBuilder.append(str);
                stringBuilder.append(this.mIndentationPattern);
                stringBuilder.append("{\n");
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(this.mIndentationPattern);
                format(jSONObject, stringBuilder, stringBuilder2.toString());
                stringBuilder.append(str);
                stringBuilder.append(this.mIndentationPattern);
                stringBuilder.append("}");
            } else if (opt instanceof JSONArray) {
                JSONArray jSONArray2 = (JSONArray) opt;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(this.mIndentationPattern);
                formatArray(jSONArray2, stringBuilder, stringBuilder2.toString());
            } else {
                formatValue(opt, stringBuilder);
            }
            if (i < jSONArray.length() - 1) {
                stringBuilder.append(",");
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append(str);
        stringBuilder.append("]");
    }

    private void formatValue(Object obj, StringBuilder stringBuilder) {
        if (obj instanceof String) {
            String replaceAll = ((String) obj).replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", Matcher.quoteReplacement("\\\"")).replaceAll("/", "\\\\/").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
            stringBuilder.append("\"");
            stringBuilder.append(replaceAll);
            stringBuilder.append("\"");
        } else if (obj instanceof Long) {
            stringBuilder.append((Long) obj);
        } else if (obj instanceof Integer) {
            stringBuilder.append((Integer) obj);
        } else if (obj instanceof Boolean) {
            stringBuilder.append((Boolean) obj);
        } else if (obj instanceof Double) {
            stringBuilder.append((Double) obj);
        } else {
            stringBuilder.append(obj.toString());
        }
    }
}