package no.knubo.accounting.client;

public class RegexpTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("15 mai 2009".matches("\\d\\d? \\w+ 20\\d\\d"));
        
        System.out.println("Bingo [[FrittRegnskapBeta Les mer]] Lotto".replaceAll("\\[\\[(\\w+)\\s(.+)\\]\\]", "<a href=\"/wakka/$1\">$2</a>"));
    }

}
