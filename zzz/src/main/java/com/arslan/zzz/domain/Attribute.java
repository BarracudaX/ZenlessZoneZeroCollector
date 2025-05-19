package com.arslan.zzz.domain;

public enum Attribute {

    ELECTRIC,ETHER,FIRE,FROST,ICE(FROST),PHYSICAL;

    private Attribute(Attribute subAttribute){

    }

    private Attribute(){

    }
}
