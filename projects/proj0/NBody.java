/**
 * Created by Emily on 1/18/18.
 */
public class NBody {

    public static double readRadius(String planetsTxtPath) {
        In in = new In(planetsTxtPath);
        int numP = in.readInt();
        double radius = in.readDouble();
        return radius;
    }

    public static Planet [] readPlanets(String planetsTxtPath) {
        In in = new In(planetsTxtPath);
        int numP = in.readInt();
        double radius = in.readDouble();
        Planet[] planets = new Planet[numP];
        for (int i = 0; i < numP; i++) {
            double xp = in.readDouble();
            double yp = in.readDouble();
            double xv = in.readDouble();
            double yv = in.readDouble();
            double m = in.readDouble();
            String img = in.readString();
            Planet p = new Planet(xp, yp, xv, yv, m, img);
            planets[i] = p;
        }
        return planets;
    }

    public static void main(String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        Planet[] planets = readPlanets(filename);
        double radius = readRadius(filename);

        StdDraw.setScale(-radius, radius);
        StdDraw.picture(0, 0, "images/starfield.jpg");
        StdAudio.play("audio/2001.mid");
        for (Planet p: planets) {
            p.draw();
        }


        StdDraw.enableDoubleBuffering();

        for (double time = 0; time < T; time += dt) {
            double[] xForces = new double[planets.length];
            double[] yForces = new double[planets.length];
            int i = 0;
            for (Planet p: planets) {
                double xF = p.calcNetForceExertedByX(planets);
                xForces[i] = xF;
                double yF = p.calcNetForceExertedByY(planets);
                yForces[i] = yF;
                i++;
            }

            int j = 0;
            for (Planet p: planets) {
                p.update(dt, xForces[j], yForces[j]);
                j++;
            }

            StdDraw.picture(0, 0, "images/starfield.jpg");
            for (Planet p: planets) {
                p.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
        }

        StdOut.printf("%d\n", planets.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < planets.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                    planets[i].yyVel, planets[i].mass, planets[i].imgFileName);
        }
    }
}
