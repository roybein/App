package com.example.sheetshowtry.app;

import java.util.Objects;

public interface IActivity {

    public abstract void init();

    public abstract void refresh(Object... params);
}