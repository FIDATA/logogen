<?xml version="1.0" encoding="utf-8"?>
<gresources xmlns:gsp="http://groovy.codehaus.org/2005/gsp">
    <gsp:scriptlet>sizes.each{ size -></gsp:scriptlet>
        <gresource prefix="$group/$appName/icons/${ size }x${ size }/apps">
            <file>${ logoName }.png</file>
        </gresource>
    <gsp:scriptlet>}</gsp:scriptlet>
    <gresource prefix="$group/$appName/icons/scalable/apps">
        <file>${ logoName }.svg</file>
    </gresource>
</gresources>
