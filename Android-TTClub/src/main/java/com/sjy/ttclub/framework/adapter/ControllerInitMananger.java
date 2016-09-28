/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.adapter;

import android.app.Activity;

import com.sjy.ttclub.TTClubController;
import com.sjy.ttclub.account.AccountController;
import com.sjy.ttclub.account.order.OrderListController;
import com.sjy.ttclub.collect.CollectController;
import com.sjy.ttclub.comment.CommentController;
import com.sjy.ttclub.community.CommunityController;
import com.sjy.ttclub.guide.GuideController;
import com.sjy.ttclub.homepage.HomepageController;
import com.sjy.ttclub.knowledge.KnowledgeController;
import com.sjy.ttclub.photopicker.PhotoPickerController;
import com.sjy.ttclub.photopreview.PhotoPreviewController;
import com.sjy.ttclub.record.RecordController;
import com.sjy.ttclub.shopping.logistics.OrderLogisticsController;
import com.sjy.ttclub.shopping.order.OrderController;
import com.sjy.ttclub.shopping.orderdetail.OrderDetailController;
import com.sjy.ttclub.shopping.product.ProductDetailController;
import com.sjy.ttclub.share.ShareController;
import com.sjy.ttclub.shopping.ShoppingController;
import com.sjy.ttclub.shopping.shoppingcar.ShoppingCarController;
import com.sjy.ttclub.splashscreen.SplashController;
import com.sjy.ttclub.web.WebViewController;

public class ControllerInitMananger {

    public static void initControllers(Activity activity) {
        new ShareController();
        new TTClubController();
        new SplashController();
        new HomepageController();
        new ShoppingController();
        new OrderController();
        new AccountController();
        new CommunityController();
        new CommentController();
        new RecordController();
        new PhotoPreviewController();
        new PhotoPickerController();
        new KnowledgeController();
        new CollectController();
        new ProductDetailController();
        new ShoppingCarController();
        new GuideController();
        new OrderListController();
        new OrderDetailController();
        new WebViewController();
        new OrderLogisticsController();
    }
}
