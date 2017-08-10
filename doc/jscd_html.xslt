<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="html" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd" indent="yes"/>
<xsl:template match="clone_list">
    <html>
        <head>
            <script type="text/javascript">
            function toggleCodeSection(btn, id, url) {
                area = document.getElementById(id);
                if (area.style.display == 'none') {
                    btn.innerHTML = '-';
                    area.style.display = 'inline';
                    var iframe = document.createElement('iframe');
                    iframe.id = 'code'+id;
                    iframe.src = url;
                    iframe.width = '600px';
                    iframe.height = 15*id.split('_')[4];
                    area.appendChild(iframe);
                } else {
                    btn.innerHTML = '+';
                    area.style.display = 'none';
                    var iframe = document.getElementById('code'+id);
                    area.removeChild(iframe);
                }
            }
        </script>
        <style>
            .Header		{ background-color: #0062C3; color: #FFFFFF; }
            .ItemNumber	{ background-color: #348BD1; color: #FFFFFF; }
        </style>
    </head>
    <body>
        <h2 align="center" style="font-family:helvetica">Summary of Code Clone</h2>
        <p/>
        <table align="center" style="font-family:helvetica" cellpadding="4">
            <xsl:for-each select="//cloneset">
            <xsl:sort data-type="number" order="descending" select="@lineoffset"/>
            <tr>
                <td class="Header" valign="top">
                    <xsl:value-of select="position()"/>
                </td>
                <td class="ItemNumber" valign="top">
                    <br/>
                    <p>Files</p>
                    <p>Locations</p>
                    <p># Lines</p>
                    <p>Code</p>
                </td>
                <xsl:for-each select="clonepart">
                    <td style="word-wrap:break-word; max-width:600px;" valign="top">
                        <p>
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="@file"/>.html#line<xsl:value-of select="@lineno"/>
                                </xsl:attribute>
                                <xsl:value-of select="@file"/>
                            </a>
                        </p>
                        <p>line <xsl:value-of select="@lineno"/>-<xsl:value-of select="@lineno+@lineoffset+(-1)"/>
                    </p>
                    <p># lines : <xsl:value-of select="@lineoffset"/>
                </p>
                <button class="ExpandButton">
                    <xsl:attribute name="onclick">
                                                                                                                                                                                      toggleCodeSection(this,
                                                                                                                                                                                              '<xsl:value-of select="../@id"/>_<xsl:value-of select="position()"/>_<xsl:value-of select="@index"/>_<xsl:value-of select="@lineno"/>_<xsl:value-of select="@lineoffset"/>',
                                                                                                                                                                                              '<xsl:value-of select="@file"/>.html#line<xsl:value-of select="@lineno"/>')
                                                                                                                                                                                          </xsl:attribute>
                                                                                                                                                                                          +</button>
                        <div style="display:none;">
                            <xsl:attribute name="id">
                                <xsl:value-of select="../@id"/>_<xsl:value-of select="position()"/>_<xsl:value-of select="@index"/>_<xsl:value-of select="@lineno"/>_<xsl:value-of select="@lineoffset"/>
                            </xsl:attribute>
                        </div>
                    </td>
                </xsl:for-each>
            </tr>
            <tr>
                <td colspan="100%">
                    <hr/>
                </td>
            </tr>
        </xsl:for-each>
    </table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
