package pl.grzegorz2047.openguild.antilogout;

/**
 * Created by grzegorz2047 on 28.08.2017.
 */
public class AntiLogoutBarGenerator {

    String generateActionBarAntilogout(long time) {
        StringBuilder out = new StringBuilder();
        out.append("§6§lFairPlay Checker");
        if (time == 0) {
            out.append("§4▉▉▉▉▉▉▉▉▉▉");
            out.append("§e ").append(time).append(" §6sec.");
            return out.toString();
        }
        float perc = ((float) time / (float) Fight.COOLDOWN) * 100;
        //System.out.println("Perc: " + perc);
        out.append(makeHealthString(perc));
        out.append("§e ").append(time).append(" §6sec.");
        return out.toString();
    }

    private String makeHealthString(float perc) {
        String splitColor = "§4";
        String firstColor = "§c";
        String defaultAntiLogoutBar = "▉▉▉▉▉▉▉▉▉▉";
        if (perc <= 10) {
            return splitColor + defaultAntiLogoutBar;
        }
        int splitPosition = ((int) perc / 10) % 10;
        int splitIndexFixed = splitPosition - 1;
        String partBar = defaultAntiLogoutBar.substring(0, splitIndexFixed) + splitColor;
        String finalBar = partBar + defaultAntiLogoutBar.substring(splitIndexFixed);
        finalBar = firstColor + finalBar;
        return finalBar;
    }
}
