package org.ow2.sirocco.apis.rest.cimi.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiHref;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;

public class AmaEtudeValidatorTest {
    private CimiRequest request;

    private CimiResponse response;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setContext(new CimiContextImpl(this.request));
        this.request.setBaseUri("http://www.test.org/");
        this.response = new CimiResponse();
    }

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

    @Test
    public void testReflectFields() throws Exception {
        this.printFields(CimiMachineImage.class);
        this.printFields(CimiMachine.class);
        this.printFields(CimiMachineTemplate.class);
        this.printFields(CimiMachineCreate.class);
    }

    @Test
    public void testReflectAnnotations() throws Exception {
        this.printAnnotations(CimiMachineImage.class);
        this.printAnnotations(CimiMachine.class);
        this.printAnnotations(CimiMachineTemplate.class);
        this.printAnnotations(CimiMachineCreate.class);
    }

    public void printAnnotations(final Class<?> klass) throws Exception {
        Annotation[] annotations;
        System.out.println(klass.getSimpleName());
        annotations = klass.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            System.out.print("\t");
            System.out.print(annotation.annotationType().getSimpleName());
            System.out.print(" : ");
            System.out.print(annotation.toString());
            System.out.println();
        }
        annotations = klass.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.print("\t");
            System.out.print(annotation.annotationType().getSimpleName());
            System.out.print(" : ");
            System.out.print(annotation.toString());
            System.out.println();
        }
    }

    public void printAnnotations(final Field field) throws Exception {
        Annotation[] annotations;
        System.out.println(field.getName());
        annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            System.out.print("\t");
            System.out.print(annotation.annotationType().getSimpleName());
            System.out.print(" : ");
            System.out.print(annotation.toString());
            System.out.println();
        }
        annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.print("\t");
            System.out.print(annotation.annotationType().getSimpleName());
            System.out.print(" : ");
            System.out.print(annotation.toString());
            System.out.println();
        }
    }

    public void printFields(final Class<?> klass) throws Exception {
        Field[] fields;
        System.out.println(klass.getSimpleName());
        fields = klass.getDeclaredFields();
        for (Field field : fields) {
            System.out.print("\t");
            System.out.print(field.getName());
            System.out.print(" : ");
            System.out.print(field.getType());
            System.out.println();
            if (field.getType().isArray()) {
                System.out.print("\t\t");
                System.out.print("Array of ");
                System.out.print(field.getType().getComponentType().getSimpleName());
                System.out.println();
                if (true == CimiHref.class.isAssignableFrom(field.getType().getComponentType())) {
                    System.out.print("\t\t\t");
                    System.out.println("Assignable from CimiHref");
                }
            } else {
                if (true == CimiHref.class.isAssignableFrom(field.getType())) {
                    System.out.print("\t\t");
                    System.out.println("Assignable from CimiHref");
                }
            }
            this.printAnnotations(field);
        }
        Set<String> names = this.findNameAssociateEntities(klass);
        System.out.print("  ");
        System.out.print(klass.getSimpleName());
        System.out.print(":");
        for (String name : names) {
            System.out.print(" ");
            System.out.print(name);
        }
        System.out.println();
        // fields = CimiMachineImage.class.getFields();
        // for (Field field : fields) {
        // System.out.println(field.getName());
        // }
    }

    @Test
    public void testValidateToCreateCimiMachineImage() throws Exception {
        CimiMachineImage toTest;

        toTest = new CimiMachineImage();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));

        toTest = new CimiMachineImage("foo");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));

        toTest = new CimiMachineImage(new ImageLocation("toto"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));

        toTest = new CimiMachineImage(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname()
            + "/1");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));

        toTest = new CimiMachineImage("foo");
        toTest.setImageLocation(new ImageLocation("toto"));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));

    }

    @Test
    public void testValidateToWriteCimiMachineImage() throws Exception {
        CimiMachineImage toTest;

        toTest = new CimiMachineImage();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, toTest));

        toTest = new CimiMachineImage("foo");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, toTest));

        toTest = new CimiMachineImage(new ImageLocation("toto"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, toTest));

        toTest = new CimiMachineImage(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname()
            + "/1");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, toTest));

        toTest = new CimiMachineImage("foo");
        toTest.setImageLocation(new ImageLocation("toto"));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, toTest));

    }

    @Test
    public void testValidateToCreateCimiMachineTemplate() throws Exception {
        CimiMachineTemplate toTest;

        // toTest = new CimiMachineTemplate();
        // Assert.assertFalse(this.validateToCreate(this.request, this.response,
        // toTest));
        //
        // toTest = new CimiMachineTemplate();
        // toTest.setHref("foo");
        // Assert.assertFalse(this.validateToCreate(this.request, this.response,
        // toTest));
        //
        // toTest = new CimiMachineTemplate();
        // toTest.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f,
        // "unit", 1), new CimiMemory(1, "unit")));
        // toTest.setMachineImage(new CimiMachineImage(new
        // ImageLocation("foo")));
        // Assert.assertTrue(this.validateToCreate(this.request, this.response,
        // toTest));
        //
        // toTest = new CimiMachineTemplate();
        // toTest.setHref(this.request.getBaseUri() +
        // CimiEntityType.MachineTemplate.getPathType().getPathname() + "/1");
        // Assert.assertTrue(this.validateToCreate(this.request, this.response,
        // toTest));
        //
        // toTest = new CimiMachineTemplate();
        // toTest.setMachineConfig(new CimiMachineConfiguration());
        // toTest.setMachineImage(new CimiMachineImage(new
        // ImageLocation("foo")));
        // Assert.assertFalse(this.validateToCreate(this.request, this.response,
        // toTest));
        //
        // toTest = new CimiMachineTemplate();
        // toTest.setHref(this.request.getBaseUri() +
        // CimiEntityType.MachineTemplate.getPathType().getPathname() + "/1");
        // toTest.setMachineConfig(new CimiMachineConfiguration());
        // toTest.setMachineImage(new CimiMachineImage(new
        // ImageLocation("foo")));
        // Assert.assertFalse(this.validateToCreate(this.request, this.response,
        // toTest));

        toTest = new CimiMachineTemplate();
        toTest.setMachineConfig(new CimiMachineConfiguration(this.request.getBaseUri()
            + CimiEntityType.MachineConfiguration.getPathType().getPathname() + "/1"));
        toTest.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));

        toTest = new CimiMachineTemplate();
        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineTemplate.getPathType().getPathname() + "/1");
        toTest.setMachineConfig(new CimiMachineConfiguration());
        toTest.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));

        toTest = new CimiMachineTemplate();
        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineTemplate.getPathType().getPathname() + "/1");
        toTest.setMachineConfig(new CimiMachineConfiguration());
        toTest.setMachineImage(new CimiMachineImage(new ImageLocation()));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.request, this.response, toTest));
    }

    public Set<String> findNameAssociateEntities(final Class<?> klass) {
        Set<String> associates = new HashSet<String>();
        Field[] fields;
        fields = klass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isArray()) {
                if (true == CimiHref.class.isAssignableFrom(field.getType().getComponentType())) {
                    associates.add(field.getName());
                }
            } else {
                if (true == CimiHref.class.isAssignableFrom(field.getType())) {
                    associates.add(field.getName());
                }
            }
        }
        return associates;
    }

    public Set<Field> findAssociateEntities(final Class<?> klass) throws Exception {
        Set<Field> associates = new HashSet<Field>();
        Field[] fields;
        fields = klass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isArray()) {
                if (true == CimiHref.class.isAssignableFrom(field.getType().getComponentType())) {
                    associates.add(field);
                }
            } else {
                if (true == CimiHref.class.isAssignableFrom(field.getType())) {
                    associates.add(field);
                }
            }
        }
        return associates;
    }

}
