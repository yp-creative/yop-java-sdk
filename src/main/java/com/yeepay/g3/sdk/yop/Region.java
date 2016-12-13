package com.yeepay.g3.sdk.yop;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: 机房<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 16:07
 */
public enum Region {

    CN_N1("sj"),
    CN_N2("dj");

    private List<String> regionIds;

    Region(String... regionIds) {
        checkNotNull(regionIds, "regionIds should not be null.");
        checkArgument(regionIds.length > 0, "regionIds should not be empty");
        this.regionIds = Arrays.asList(regionIds);
    }

    @Override
    public String toString() {
        return this.regionIds.get(0);
    }

    public static Region fromValue(String regionId) {
        checkNotNull(regionId, "regionId should not be null.");
        for (Region region : Region.values()) {
            List<String> regionIds = region.regionIds;
            if (regionIds != null && regionIds.contains(regionId)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Cannot create region from " + regionId);
    }

}
