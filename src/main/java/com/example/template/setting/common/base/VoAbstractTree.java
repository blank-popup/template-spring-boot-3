package com.example.template.setting.common.base;

import java.util.ArrayList;
import java.util.List;

public abstract class VoAbstractTree {
    private List<VoAbstractTree> items = new ArrayList<VoAbstractTree>();
    private Integer level = 1;

    public abstract Long id();

    public abstract Long parentsId();

    public void addChild(VoAbstractTree child) {
        child.addLevel(this.level);
        this.items.add(child);
    }

    private void addLevel(int parentsLevel) {
        this.level = parentsLevel + 1;
        this.items.forEach(vo -> {
            vo.addLevel(this.level);
        });
    }
}
