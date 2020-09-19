////
////  ViewController.swift
////  Details of a face
////
////  Created by ebukaa on 13/10/2018.
////  Copyright © 2018 ebukaa. All rights reserved.
////
//
//import UIKit
//import CoreImage
//import Vision
//
//
//
//class OnStaticImageViewController: UITableViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
//
//    //    MARK: Outlet variables
//    @IBOutlet weak var imageView: UIImageView!
//    @IBOutlet weak var textView: UITextView!
//
//    //    MARK: Action functions
//    @IBAction func importImage(_ sender: UIButton) {
//
//        //        create image picker
//        let imagePicker = UIImagePickerController()
//
//        imagePicker.delegate = self
//        imagePicker.sourceType = UIImagePickerController.SourceType.photoLibrary
//
//        //        display image picker
//        self.present(imagePicker, animated: true, completion: nil)
//    }
//
//    //    MARK: Override functions
//    override func viewDidLoad() {
//        super.viewDidLoad()
//
//        detectFace()
//    }
//}
//
//// MARK: UIImagePickerControllerDelegate functions
//extension OnStaticImageViewController {
//    //    pick photo
//    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
//
//        if let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
//            imageView.image = image
//        }
//        detectFace()
//
//        dismiss(animated: true, completion: nil)
//    }
//}
//
//// MARK: Helper functions
//extension OnStaticImageViewController {
//    func detectFace() {
//
//        guard let image = imageView.image else {
//            return
//        }
//
//        //        get image from image view
//        guard let ciImage = CIImage(image: image) else {
//            return
//        }
//
//        //        setup detector
//        let accuracy = [CIDetectorAccuracy: CIDetectorAccuracyHigh]
//
//        guard let faceDetector = CIDetector(ofType: CIDetectorTypeFace, context: nil, options: accuracy) else {
//            return
//        }
//        let faces = faceDetector.features(in: ciImage, options: [CIDetectorSmile: true])
//
//        if !faces.isEmpty {
//            for face in faces as! [CIFaceFeature] {
//                let mouthShowing = "\nMouth is showing: \(face.hasMouthPosition)"
//                let isSmiling = "\nPerson is smiling: \(face.hasSmile)"
//
//                var bothEyesShown = true
//
//                if !face.hasRightEyePosition || !face.hasLeftEyePosition {
//                    bothEyesShown = false
//                }
//                let bothEyesShowing = "\nBoth eyes showing: \(bothEyesShown)"
//
//                //                degree of suspiciousness
//                let array = ["low", "medium", "high", "very high"]
//
//                var suspectLevel = 0
//
//                if !face.hasMouthPosition {
//                    suspectLevel += 1
//                }
//                if !face.hasSmile {
//                    suspectLevel += 1
//                }
//                if bothEyesShowing.contains("false") {
//                    suspectLevel += 1
//                }
//                if face.faceAngle > 10 || face.faceAngle < -10 {
//                    suspectLevel += 1
//                }
//
//                let suspectText = "\nSuspiciousness: \(array[suspectLevel])"
//
//                textView.text = "\(suspectText) \n\(mouthShowing) \(isSmiling) \(bothEyesShowing)"
//            }
//        } else {
//            textView.text = "\nThere is no face detected"
//        }
//    }
//}



//
//  ViewController.swift
//  Face Recognition Project
//
//  Created by ebukaa on 14/02/2019.
//  Copyright © 2019 ebukaa. All rights reserved.
//

import UIKit
import Vision

class OnStaticImageViewController: UITableViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    //    MARK: Class variables
    let imagePicker = UIImagePickerController()

    //    MARK: Outlet variables
    @IBOutlet weak var imageView: UIImageView!

    //    MARK: Action functions
    @IBAction func captureOrImportImage(_ sender: UIButton) {

        let tabBar = tabBarController as! TabBarViewController
        tabBar.image = nil

        let alert = UIAlertController(title: nil, message: "Use camera or import from library", preferredStyle: .alert)

        //        actions
        let camera = UIAlertAction(title: "Camera", style: .default) { (UIAlertAction) in

            self.useCamera()
        }
        let photoLibrary = UIAlertAction(title: "Photo Library", style: .default) { (UIAlertAction) in

            self.usePhotoLibrary()
        }
        alert.addAction(camera)
        alert.addAction(photoLibrary)

        present(alert, animated: true, completion: nil)
    }

    //    MARK: Override (start)
    override func viewDidLoad() {
        super.viewDidLoad()

//        imagePicker
        imagePicker.delegate = self
        imagePicker.allowsEditing = false

        process()
    }
    override func viewWillAppear(_ animated: Bool) {

        let tabBar = tabBarController as! TabBarViewController

        guard tabBar.image != nil else {
            return
        }
        imageView.image = tabBar.image
        process()
    }
}

// MARK: UIImagePickerControllerDelegate functions
extension OnStaticImageViewController {
//    pick photo
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {

        if let userPickedImage = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
            imageView.image = userPickedImage
        }
        process()

        imagePicker.dismiss(animated: true, completion: nil)
    }
}

// MARK: Helper functions
extension OnStaticImageViewController {
    func useCamera() {

        imagePicker.sourceType = .camera
        displayImagePicker()
    }
    func usePhotoLibrary() {

        imagePicker.sourceType = .photoLibrary
        displayImagePicker()
    }
    func displayImagePicker() {
        self.present(imagePicker, animated: true, completion: nil)
    }
    func imageSelectionAlert() {

        let alert = UIAlertController(title: nil, message: "Use camera or import from library", preferredStyle: .alert)

        //        actions
        let camera = UIAlertAction(title: "Camera", style: .default) { (UIAlertAction) in

            self.useCamera()
        }
        let photoLibrary = UIAlertAction(title: "Photo Library", style: .default) { (UIAlertAction) in

            self.usePhotoLibrary()
        }
        alert.addAction(camera)
        alert.addAction(photoLibrary)

        present(alert, animated: true, completion: nil)
    }
    func process() {

        var orientation:Int32 = 0

        guard let image = imageView.image else {
            return
        }

        // detect image orientation, we need it to be accurate for the face detection to work
        switch image.imageOrientation {
        case .up:
            orientation = 1
        case .right:
            orientation = 6
        case .down:
            orientation = 3
        case .left:
            orientation = 8
        default:
            orientation = 1
        }

//        vision
        let faceLandmarksRequest = VNDetectFaceLandmarksRequest(completionHandler: self.handleFaceFeatures)

        let requestHandler = VNImageRequestHandler(cgImage: image.cgImage!, orientation: CGImagePropertyOrientation(rawValue: CGImagePropertyOrientation.RawValue(orientation))! ,options: [:])

        do {
            try requestHandler.perform([faceLandmarksRequest])
        } catch {
            print(error)
        }
    }
    func handleFaceFeatures(request: VNRequest, errror: Error?) {

        guard let observations = request.results as? [VNFaceObservation] else {
            fatalError("unexpected result type!")
        }

        for face in observations {
            addFaceLandmarksToImage(face)
        }
    }

    func addFaceLandmarksToImage(_ face: VNFaceObservation) {

        guard let image = imageView.image else {
            return
        }
        UIGraphicsBeginImageContextWithOptions(image.size, true, 0.0)
        let context = UIGraphicsGetCurrentContext()

        // draw the image
        image.draw(in: CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height))

        context?.translateBy(x: 0, y: image.size.height)
        context?.scaleBy(x: 1.0, y: -1.0)

        // draw the face rect
        let w = face.boundingBox.size.width * image.size.width
        let h = face.boundingBox.size.height * image.size.height
        let x = face.boundingBox.origin.x * image.size.width
        let y = face.boundingBox.origin.y * image.size.height
        let faceRect = CGRect(x: x, y: y, width: w, height: h)
        context?.saveGState()
        context?.setStrokeColor(UIColor.red.cgColor)
        context?.setLineWidth(8.0)
        context?.addRect(faceRect)
        context?.drawPath(using: .stroke)
        context?.restoreGState()

        // face contour
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.faceContour {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // outer lips
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.outerLips {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.closePath()
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // inner lips
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.innerLips {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.closePath()
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // left eye
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.leftEye {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.closePath()
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // right eye
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.rightEye {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.closePath()
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // left pupil
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.leftPupil {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.closePath()
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // right pupil
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.rightPupil {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.closePath()
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // left eyebrow
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.leftEyebrow {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // right eyebrow
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.rightEyebrow {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // nose
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.nose {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.closePath()
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // nose crest
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.noseCrest {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // median line
        context?.saveGState()
        context?.setStrokeColor(UIColor.yellow.cgColor)
        if let landmark = face.landmarks?.medianLine {
            for i in 0...landmark.pointCount - 1 { // last point is 0,0
                let point = landmark.normalizedPoints[i] //.point(at: i)
                if i == 0 {
                    context?.move(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                } else {
                    context?.addLine(to: CGPoint(x: x + CGFloat(point.x) * w, y: y + CGFloat(point.y) * h))
                }
            }
        }
        context?.setLineWidth(8.0)
        context?.drawPath(using: .stroke)
        context?.saveGState()

        // get the final image
        let finalImage = UIGraphicsGetImageFromCurrentImageContext()

        // end drawing context
        UIGraphicsEndImageContext()

        imageView.image = finalImage
    }
}

