package haven;

public class StudyWnd extends Window {
    private Widget study;
    private Widget studyInfo;

    public StudyWnd(Coord sz) {
        super(sz, "Study");
    }

    public void setStudy(Inventory inv) {
        if (study != null) {
            study.destroy();
            studyInfo.destroy();
        }



        final InventoryProxy invp = new InventoryProxy(inv);
        invp.setLocked(Config.studylock);
        add(new CheckBox("Lock") {
                {
                    a = Config.studylock;
                }

                public void set(boolean val) {
                    super.set(val);
                    invp.setLocked(val);
                    Utils.setprefb("studylock", val);
                    Config.studylock = val;
                    a = val;
                }
            }, new Coord(0, 0));
        add(new CheckBox("Auto") {
                {
                    a = Config.autostudy;
                }

                public void set(boolean val) {
                    Utils.setprefb("autostudy", val);
                    Config.autostudy = val;
                    a = val;
                }
            }, new Coord(55, 0));
        study = add(invp, 0, 20);
        studyInfo = add(new StudyInfo(new Coord(study.sz.x, 50), inv), 0, study.c.y + study.sz.y + 5);
        pack();
    }

    @Override
    public void hide() {
        super.hide();
        Config.setStudyVisible(false);
    }

    @Override
    public void show() {
        super.show();
        Config.setStudyVisible(true);
    }

@Override
     public boolean mouseup(Coord c, int button) {
         if (isGrabbed()) {
             Config.setStudyPosition(this.c);
         }
         return super.mouseup(c, button);
     }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == this && msg.equals("close")) {
            hide();
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    private class StudyInfo extends Widget {
        public Widget study;
        public int texp, tw, tenc;
        private final Text.UText<?> texpt = new Text.UText<Integer>(Text.std) {
            public Integer value() {return(texp);}
            public String text(Integer v) {return(Utils.thformat(v));}
        };
        private final Text.UText<?> twt = new Text.UText<String>(Text.std) {
            public String value() {return(tw + "/" + ui.sess.glob.cattr.get("int").comp);}
        };
        private final Text.UText<?> tenct = new Text.UText<Integer>(Text.std) {
            public Integer value() {return(tenc);}
            public String text(Integer v) {return(Integer.toString(tenc));}
        };

        private StudyInfo(Coord sz, Widget study) {
            super(sz);
            this.study = study;
            add(new Label("Attention:"), 2, 2);
            add(new Label("Experience cost:"), 2, 18);
            add(new Label("Learning points:"), 2, 34);
        }

        private void upd() {
            int texp = 0, tw = 0, tenc = 0;
            for(GItem item : study.children(GItem.class)) {
                try {
                    Curiosity ci = ItemInfo.find(Curiosity.class, item.info());
                    if(ci != null) {
                        texp += ci.exp;
                        tw += ci.mw;
                        tenc += ci.enc;
                    }
                } catch(Loading l) {
                }
            }
            this.texp = texp; this.tw = tw; this.tenc = tenc;
        }

        public void draw(GOut g) {
            upd();
            super.draw(g);
            g.chcolor(255, 192, 255, 255);
            g.aimage(twt.get().tex(), new Coord(sz.x - 4, 2), 1.0, 0.0);
            g.chcolor(255, 255, 192, 255);
            g.aimage(tenct.get().tex(), new Coord(sz.x - 4, 18), 1.0, 0.0);
            g.chcolor(192, 192, 255, 255);
            g.aimage(texpt.get().tex(), new Coord(sz.x - 4, 34), 1.0, 0.0);
        }
    }
}
