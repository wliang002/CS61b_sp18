/**
 * Created by Emily on 1/19/18.
 */
public class Planet {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;
    private static final double G = 6.67E-11D;

    /*
     * Planet constructor.
     */
    public Planet(double xP, double yP, double xV,
                  double yV, double m, String img) {
        this.xxPos = xP;
        this.yyPos = yP;
        this.xxVel = xV;
        this.yyVel = yV;
        this.mass = m;
        this.imgFileName = img;
    }

    /*
     * Copy of planet P.
     */
    public Planet(Planet p) {
        this.xxPos = p.xxPos;
        this.yyPos = p.yyPos;
        this.xxVel = p.xxVel;
        this.yyVel = p.yyVel;
        this.mass = p.mass;
        this.imgFileName = p.imgFileName;
    }

    /*
     * calculates the distance between P and another planet.
     */
    public double calcDistance(Planet p) {
        double dx = p.xxPos - this.xxPos;
        double dy = p.yyPos - this.yyPos;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /*
     * Returns a double describing the force exerted
     * on P by the given planet.
     */
    public double calcForceExertedBy(Planet p) {
        double rsqr = this.calcDistance(p) * this.calcDistance(p);
        return G * this.mass * p.mass / rsqr;
    }

    /*
     * the force exerted in the X directions.
     */
    public double calcForceExertedByX(Planet p) {
        double dx = p.xxPos - this.xxPos;
        return this.calcForceExertedBy(p) * dx / this.calcDistance(p);
    }

    /*
     * the force exerted in the Y directions.
     */
    public double calcForceExertedByY(Planet p) {
        double dy = p.yyPos - this.yyPos;
        return this.calcForceExertedBy(p) * dy / this.calcDistance(p);
    }

    /*
     * calculate the net X force exerted by all planets in PLANETS.
     */
    public double calcNetForceExertedByX(Planet[] planets) {
        double netX = 0;
        for (Planet p: planets) {
            if (!p.equals(this)) {
                netX += this.calcForceExertedByX(p);
            }
        }
        return netX;
    }

    /*
     * calculate the net Y force exerted by all planets in PLANETS.
     */
    public double calcNetForceExertedByY(Planet[] planets) {
        double netY = 0;
        for (Planet p: planets) {
            if (!p.equals(this)) {
                netY += this.calcForceExertedByY(p);
            }
        }
        return netY;
    }


    /*
         * change in the planet’s velocity and position if
         * an x-force of FX and a y-force of FY were applied for DT
         * period of time.
         */
    public void update(double dt, double fX, double fY) {
        double aX = fX / mass;
        double aY = fY / mass;
        xxVel = xxVel + dt * aX;
        yyVel = yyVel + dt * aY;
        xxPos = xxPos + dt * xxVel;
        yyPos = yyPos + dt * yyVel;
    }

    /*
     * Draw
     */
    public void draw() {
        StdDraw.picture(xxPos, yyPos, "./images/" + imgFileName);
    }


}
