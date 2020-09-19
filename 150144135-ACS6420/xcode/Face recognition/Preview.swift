//
//  Preview.swift
//  Face detection
//
//  Created by ebukaa on 13/10/2018.
//  Copyright © 2018 ebukaa. All rights reserved.
//

import UIKit
import AVFoundation
import Vision

class Preview: UIView {

    //    MARK: Class variables
    var videoPreviewLayer: AVCaptureVideoPreviewLayer {
        return layer as! AVCaptureVideoPreviewLayer
    }
    var session: AVCaptureSession? {
        get {
            return videoPreviewLayer.session
        }
        set {
            videoPreviewLayer.session = newValue
        }
    }
    var maskLayer = [CAShapeLayer]()
    
    //    MARK: Override
    override class var layerClass: AnyClass {
        return AVCaptureVideoPreviewLayer.self
    }
}

// MARK: Helper functions (methods)
extension Preview {
    func removeMask() {
        
        for mask in maskLayer {
            mask.removeFromSuperlayer()
        }
        maskLayer.removeAll()
    }
    
//    Create a new layer for drawing the bounding box
    func createLayer(in rect: CGRect) -> CAShapeLayer {
        
        let mask = CAShapeLayer()
        
        mask.frame = rect
        mask.cornerRadius = 10
        mask.opacity = 0.75
        mask.borderColor = UIColor.yellow.cgColor
        mask.borderWidth = 2.0
        
        maskLayer.append(mask)
        layer.insertSublayer(mask, at: 1)
        
        return mask
    }
    func drawBoundingBox(for face: VNFaceObservation) -> CAShapeLayer {
        
        let transform = CGAffineTransform(scaleX: 1, y: -1).translatedBy(x: 0, y: -frame.height)
        
        let translate = CGAffineTransform.identity.scaledBy(x: frame.width, y: frame.height)
        
        // The coordinates are normalized to the dimensions of the processed image, with the origin at the image's lower-left corner.
        let facebounds = face.boundingBox.applying(translate).applying(transform)
        
        let faceLayer = createLayer(in: facebounds)
        
        return faceLayer
    }
    func drawLandmarks(for face: VNFaceObservation) {
        
        let faceLayer = drawBoundingBox(for: face)
        
        guard let faceLandmarks = face.landmarks else {
            return
        }
        
        // Draw the landmarks
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.nose)!, isClosed:false)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.noseCrest)!, isClosed:false)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.medianLine)!, isClosed:false)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.leftEye)!)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.leftPupil)!)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.leftEyebrow)!, isClosed:false)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.rightEye)!)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.rightPupil)!)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.rightEye)!)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.rightEyebrow)!, isClosed:false)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.innerLips)!)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.outerLips)!)
        drawLandmarks(on: faceLayer, faceLandmarkRegion: (faceLandmarks.faceContour)!, isClosed: false)
    }
    
    func drawLandmarks(on layer: CALayer, faceLandmarkRegion: VNFaceLandmarkRegion2D, isClosed: Bool = true) {
        
        let rect: CGRect = layer.frame
        var points: [CGPoint] = []
        
        for i in 0..<faceLandmarkRegion.pointCount {
            let point = faceLandmarkRegion.normalizedPoints[i]
            points.append(point)
        }
        let landmarkLayer = drawPointsOnLayer(rect: rect, landmarkPoints: points, isClosed: isClosed)
        
//        change scale, coordinate systems, and mirroring
        landmarkLayer.transform = CATransform3DMakeAffineTransform(
            
            CGAffineTransform.identity.scaledBy(x: rect.width, y: -rect.height).translatedBy(x: 0, y: -1)
        )
        layer.insertSublayer(landmarkLayer, at: 1)
    }
    
    func drawPointsOnLayer(rect:CGRect, landmarkPoints: [CGPoint], isClosed: Bool = true) -> CALayer {
        
        let linePath = UIBezierPath()
        linePath.move(to: landmarkPoints.first!)
        
        for point in landmarkPoints.dropFirst() {
            linePath.addLine(to: point)
        }
        if isClosed {
            linePath.addLine(to: landmarkPoints.first!)
        }
        let lineLayer = CAShapeLayer()
        
        lineLayer.path = linePath.cgPath
        lineLayer.fillColor = nil
        lineLayer.opacity = 1.0
        lineLayer.strokeColor = UIColor.green.cgColor
        lineLayer.lineWidth = 0.02
        
        return lineLayer
    }
}
