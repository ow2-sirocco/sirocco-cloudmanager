package org.ow2.sirocco.apis.rest.cimi.validator;

import java.net.URI;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class AmaEtudeValidatorTest {

    @Test
    public void testPattern() {
        String regex = "^[a-zA-Z_]([a-zA-Z_0-9]+)?$";
        Pattern p = Pattern.compile(regex);

        Assert.assertTrue(p.matcher("a").matches());
        Assert.assertTrue(p.matcher("z").matches());
        Assert.assertTrue(p.matcher("A").matches());
        Assert.assertTrue(p.matcher("Z").matches());
        Assert.assertTrue(p.matcher("_").matches());
        Assert.assertFalse(p.matcher("0").matches());
        Assert.assertFalse(p.matcher("9").matches());
        Assert.assertFalse(p.matcher(".").matches());
        Assert.assertFalse(p.matcher("-").matches());
        Assert.assertFalse(p.matcher("/").matches());

        Assert.assertTrue(p.matcher("_aaaaab").matches());
        Assert.assertTrue(p.matcher("_aaa999aab").matches());
        Assert.assertTrue(p.matcher("_aaa9_9_9aab").matches());
        Assert.assertTrue(p.matcher("____").matches());
        Assert.assertTrue(p.matcher("a0123456789").matches());
        Assert.assertTrue(p.matcher("Z0123456789").matches());
        Assert.assertTrue(p.matcher("_0123456789").matches());
        Assert.assertTrue(p.matcher("ABCDEF_XYZ").matches());
        Assert.assertTrue(p.matcher("abcdef_xyz").matches());

        Assert.assertFalse(p.matcher("ab.cdef_xyz").matches());
    }

    @Test
    public void testURI() throws Exception {

        URI uri;

        uri = new URI("aB5");
        this.print(uri);
        uri = new URI("èèé");
        this.print(uri);

        uri = new URI("www.ave.cesar.com");
        this.print(uri);

        uri = new URI("www.salut.fr:8789");
        this.print(uri);

        uri = new URI("http://www.mickey.mouse");
        this.print(uri);

        uri = new URI("http://www.souris.minie:1234");
        this.print(uri);

        uri = new URI("ftp://file.transfert.protocol");
        this.print(uri);

        uri = new URI("https://securit.protocol:4569?paramOne=one&paramTwo=two#coucou");
        this.print(uri);

        uri = new URI("mytp://mon.protocole.ama:9999?paramOne=one&paramTwo=two#coucou");
        this.print(uri);

        uri = new URI("http:/mon.protocole.ama:9999?paramOne=one&paramTwo=two#coucou");
        this.print(uri);
    }

    public void print(final URI uri) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append(uri.toString());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.toASCIIString():");
        sb.append(uri.toASCIIString());
        sb.append("\n");

        sb.append("\t");
        sb.append("uri.getAuthority():");
        sb.append(uri.getAuthority());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getFragment():");
        sb.append(uri.getFragment());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getHost():");
        sb.append(uri.getHost());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getPath():");
        sb.append(uri.getPath());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getPort():");
        sb.append(uri.getPort());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getQuery():");
        sb.append(uri.getQuery());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getScheme():");
        sb.append(uri.getScheme());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getSchemeSpecificPart():");
        sb.append(uri.getSchemeSpecificPart());
        sb.append("\n");
        sb.append("\t");
        sb.append("uri.getUserInfo():");
        sb.append(uri.getUserInfo());

        System.out.println(sb);

    }
}
