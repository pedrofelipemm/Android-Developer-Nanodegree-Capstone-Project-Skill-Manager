package study.pmoreira.skillmanager.ui.customview;

import android.view.View;

import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;

import java.util.ArrayList;
import java.util.List;

public class StringChip implements Chip {

    private String text;

    private StringChip(String text) {
        this.text = text;
    }

    private static List<Chip> toChipList(List<String> list) {
        List<Chip> chips = new ArrayList<>();
        for (String s : list) {
            chips.add(new StringChip(s));
        }
        return chips;
    }

    public static void setChipList(ChipView chipView, List<String> list) {
        chipView.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
        chipView.setChipList(StringChip.toChipList(list));
    }

    public static ArrayList<String> getChipList(ChipView chipView) {
        ArrayList<String> results = new ArrayList<>();
        for (Chip chip : chipView.getChipList()) {
            results.add(chip.getText());
        }

        return results;
    }

    @Override
    public String getText() {
        return text;
    }
}