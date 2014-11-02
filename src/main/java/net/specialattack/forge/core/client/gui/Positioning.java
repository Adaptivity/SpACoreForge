package net.specialattack.forge.core.client.gui;

public enum Positioning {
    CENTER {
        @Override
        int position(int pos, int size, int parentSize) {
            return (parentSize + size) / 2;
        }
    },
    CENTER_OFFSET {
        @Override
        int position(int pos, int size, int parentSize) {
            return (parentSize + size) / 2 + pos;
        }
    },
    MIN {
        @Override
        int position(int pos, int size, int parentSize) {
            return 0;
        }
    },
    MIN_OFFSET {
        @Override
        int position(int pos, int size, int parentSize) {
            return pos;
        }
    },
    MAX {
        @Override
        int position(int pos, int size, int parentSize) {
            return parentSize - size;
        }
    },
    MAX_OFFSET {
        @Override
        int position(int pos, int size, int parentSize) {
            return parentSize - size + pos;
        }
    };

    abstract int position(int pos, int size, int parentSize);

}
