package study.pmoreira.skillmanager.ui.customview;

import com.plumillonforge.android.chipview.Chip;

import java.util.ArrayList;
import java.util.List;

public class StringChip implements Chip {

    private String text;

    public StringChip(String text) {
        this.text = text;
    }

    public static List<Chip> toChipList(List<String> list) {
        List<Chip> chips = new ArrayList<>();
        for (String s : list) {
            chips.add(new StringChip(s));
        }
        return chips;
    }

    @Override
    public String getText() {
        return text;
    }
}