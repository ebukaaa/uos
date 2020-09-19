//
//  TabBarViewController.swift
//  Face Recognition Project
//
//  Created by ebukaa on 15/02/2019.
//  Copyright © 2019 ebukaa. All rights reserved.
//

import UIKit

class TabBarViewController: UITabBarController, UITabBarControllerDelegate {
    
    //    MARK: Class variables
    var image: UIImage?
    
    //    MARK: Override (start)
    override func viewDidLoad() {
        super.viewDidLoad()

        self.delegate = self
    }
}

// UITabBarControllerDelegate functions
extension TabBarViewController {
    override func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        
    }
 
}
