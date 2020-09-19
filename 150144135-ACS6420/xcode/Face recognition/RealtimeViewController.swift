//
//  ViewController.swift
//  Face detection
//
//  Created by ebukaa on 13/10/2018.
//  Copyright © 2018 ebukaa. All rights reserved.
//

import UIKit
import AVFoundation
import Vision

// MARK: Enumerators
enum SessionSetupResult {
    
    case success
    case notAuthorized
    case configurationFailed
}
enum DeviceOrientation: UInt32 {
    
    case top0ColLeft = 1
    case top0ColRight = 2
    case bottom0ColRight = 3
    case bottom0ColLeft = 4
    case left0ColTop = 5
    case right0ColTop = 6
    case right0ColBottom = 7
    case left0ColBottom = 8
}

class RealtimeViewController: UIViewController {

    //    MARK: Class variables
    var image: UIImage?
    
//    VNRequest types: Either Retangles or Landmarks
    var faceDetectionRequest = VNRequest()
    
    let session = AVCaptureSession()
    
    var requests = [VNRequest]()
    
    //    communicate with the session and other session objects on this queue
    let sessionQueue = DispatchQueue(label: "session queue", attributes: [], target: nil)
    
    var videoDataOutputQueue = DispatchQueue(label: "VideoDataOutputQueue")
    
    var setupResult: SessionSetupResult = .success
    
    var photoOutput: AVCapturePhotoOutput?
    
    var videoDeviceInput: AVCaptureDeviceInput!
    
    var videoDataOutput: AVCaptureVideoDataOutput!
    
    var isSessionRunning = false
    
    var deviceCameraPosition: AVCaptureDevice.Position = .back
    
    //    MARK: Outlet variables
    @IBOutlet weak var preview: Preview!
    
    //    MARK: Action functions
    @IBAction func updateDetectionType(_ sender: UISegmentedControl) {
        
        //        use segmented control to switch over VNRequest
        faceDetectionRequest = sender.selectedSegmentIndex == 0 ? VNDetectFaceRectanglesRequest(completionHandler: detectFaces) : VNDetectFaceLandmarksRequest(completionHandler: detectFaceLandmarks)
        
        setupVision()
    }
    @IBAction func captureButton(_ sender: UIButton) {
        
        let settings = AVCapturePhotoSettings()
        
        photoOutput?.capturePhoto(with: settings, delegate: self)
    }
    
    //    MARK: Override (start)
    override func viewDidLoad() {
        super.viewDidLoad()
        
//        setup video preview
        preview.session = session
        
        faceDetectionRequest = VNDetectFaceRectanglesRequest(completionHandler: detectFaces)
        
        setupVision()
        
//         check video authorization status.
        switch AVCaptureDevice.authorizationStatus(for: AVMediaType.video) {
        case .authorized:
            break
            
//        user has not yet been presented with the option to grant video access
        case .notDetermined:
//            suspend the session queue to delay session setup until the access request has completed
            sessionQueue.suspend()
            
            AVCaptureDevice.requestAccess(for: AVMediaType.video) { (granted) in
                
                if !granted {
                    self.setupResult = .notAuthorized
                }
                self.sessionQueue.resume()
            }
        default:
//            user denied access.
            setupResult = .notAuthorized
        }
        
//        setup the capture session.
        sessionQueue.async {
            
            self.configureSession()
        }
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        sessionQueue.async {
            
            switch self.setupResult {
            case .success:
//                setup observers and start the session running
                self.addObservers()
                self.session.startRunning()
                self.isSessionRunning = self.session.isRunning
                
            case .notAuthorized:
                DispatchQueue.main.async {
                    
                    let message = NSLocalizedString("AVCamBarcode doesn't have permission to use the camera, please change privacy settings", comment: "Alert message when the user has denied access to the camera")
                    
                    let alertController = UIAlertController(title: "AppleFaceDetection", message: message, preferredStyle: .alert)
                    
                    alertController.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Alert OK button"), style: .cancel, handler: nil))
                    
                    alertController.addAction(UIAlertAction(title: NSLocalizedString("Setting", comment: "Alert button to open Settings"), style: .default, handler: { (action) in
                        
                        UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!, options: [:], completionHandler: nil)
                    }))
                    self.present(alertController, animated: true, completion: nil)
                }
            case .configurationFailed:
                DispatchQueue.main.async {
                    
                    let message = NSLocalizedString("Unable to capture media", comment: "Alert message when something goes wrong during capture session configuration")
                    let alertController = UIAlertController(title: "AppleFaceDetection", message: message, preferredStyle: .alert)
                    
                    alertController.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Alert OK button"), style: .cancel, handler: nil))
                    
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
    }
    
//    stop running face detection when in background/close
    override func viewWillDisappear(_ animated: Bool) {
        
        sessionQueue.async {
            
            guard self.setupResult == .success else {
                return
            }
            self.session.stopRunning()
            self.isSessionRunning = self.session.isRunning
            self.removeObservers()
        }
        super.viewWillDisappear(animated)
    }
    
//    handles subsequent orientation changes
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        
        if let videoPreviewLayerConnection = preview.videoPreviewLayer.connection {
            let deviceOrientation = UIDevice.current.orientation
            
            guard let newVideoOrientation = deviceOrientation.videoOrientation,
                deviceOrientation.isPortrait || deviceOrientation.isLandscape else {
                return
            }
            videoPreviewLayerConnection.videoOrientation = newVideoOrientation
        }
    }
}

// MARK: Helper functions (methods)
extension RealtimeViewController {
    func detectFaces(request:VNRequest, error:Error?) {
        
//        perform all the UI updates on the main queue
        DispatchQueue.main.async {
            
            guard let results = request.results as? [VNFaceObservation] else {
                return
            }
            self.preview.removeMask()
            
            for face in results {
                _ = self.preview.drawBoundingBox(for: face)
            }
        }
    }
    func detectFaceLandmarks(request:VNRequest, error:Error?) {
        
//        perform all UI updates on main queue
        DispatchQueue.main.async {
            
            guard let results = request.results as? [VNFaceObservation] else {
                return
            }
            self.preview.removeMask()
            
            for face in results {
                self.preview.drawLandmarks(for: face)
            }
        }
    }
    func setupVision() {
        
        requests = [faceDetectionRequest]
    }
    func configureSession() {
        
        guard self.setupResult == .success else {
            fatalError("Setup failed")
        }
        session.beginConfiguration()
        session.sessionPreset = .high
        
//        Add video input.
        do {
            var defaultVideoDevice: AVCaptureDevice?
            
//            Choose the back dual camera if available, otherwise default to a wide angle camera.
            if let dualCameraDevice = AVCaptureDevice.default(.builtInDualCamera, for: AVMediaType.video, position: .back) {
                defaultVideoDevice = dualCameraDevice
                
            } else if let backCameraDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: AVMediaType.video, position: .back) {
                defaultVideoDevice = backCameraDevice
                
            } else if let frontCameraDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: AVMediaType.video, position: .front) {
                defaultVideoDevice = frontCameraDevice
            }
            guard let videoCamera = defaultVideoDevice else {
                fatalError("Couldn't find default video device")
            }
            let videoDeviceInput = try AVCaptureDeviceInput(device: videoCamera)
            
            if session.canAddInput(videoDeviceInput) {
                session.addInput(videoDeviceInput)
                
                self.videoDeviceInput = videoDeviceInput
                
                self.photoOutput = AVCapturePhotoOutput()
                self.photoOutput?.setPreparedPhotoSettingsArray([AVCapturePhotoSettings(format: [AVVideoCodecKey : AVVideoCodecType.jpeg])], completionHandler: nil)
                
                session.addOutput(photoOutput!)
                
                /*
                 Why are we dispatching this to the main queue?
                 Because AVCaptureVideoPreviewLayer is the backing layer for Preview and UIView
                 can only be manipulated on the main thread.
                 Note: As an exception to the above rule, it is not necessary to serialize video orientation changes
                 on the AVCaptureVideoPreviewLayer’s connection with other session manipulation.
                 */
                DispatchQueue.main.async {
                    
                    let statusBarOrientation = UIApplication.shared.statusBarOrientation
                    var initialVideoOrientation: AVCaptureVideoOrientation = .portrait
                    
                    guard statusBarOrientation == .unknown else {
                        if let videoOrientation = statusBarOrientation.videoOrientation {
//                            use status bar orientation as initial video orientation
                            initialVideoOrientation = videoOrientation
                        }
                        return
                    }
                    
//                    serialize change in video orientation on the AVCaptureVideoPreviewLayer’s connection with other session manipulation
                    self.preview.videoPreviewLayer.connection!.videoOrientation = initialVideoOrientation
                }
            } else {
                print("Could not add video device input to the session")
                
                setupResult = .configurationFailed
                session.commitConfiguration()
                return
            }
        } catch {
            print("Could not create video device input: \(error)")
            
            setupResult = .configurationFailed
            session.commitConfiguration()
            
            return
        }
        
//        add output
        videoDataOutput = AVCaptureVideoDataOutput()
        videoDataOutput.videoSettings = [(kCVPixelBufferPixelFormatTypeKey as String): Int(kCVPixelFormatType_32BGRA)]
        
        if session.canAddOutput(videoDataOutput) {
            videoDataOutput.alwaysDiscardsLateVideoFrames = true
            
            videoDataOutput.setSampleBufferDelegate(self, queue: videoDataOutputQueue)
            
            session.addOutput(videoDataOutput)
            
        } else {
            print("Could not add metadata output to the session")
            
            setupResult = .configurationFailed
            session.commitConfiguration()
            return
        }
        session.commitConfiguration()
    }
    func addObservers() {
        
        //        Observe preview's regionOfInterest to update AVCaptureMetadataOutput's rectOfInterest when user finish resizing region of interest.
        NotificationCenter.default.addObserver(self, selector: #selector(sessionRuntimeError(_:)), name: Notification.Name("AVCaptureSessionRuntimeErrorNotification"), object: session)
        
        /*
         session can only run in full screen. It will be interrupted in a multi-app layout.
         */
        
//        observers to handle session interruptions and show "preview is paused" message.
        NotificationCenter.default.addObserver(self, selector: #selector(sessionWasInterrupted(_:)), name: Notification.Name("AVCaptureSessionWasInterruptedNotification"), object: session)
        
        NotificationCenter.default.addObserver(self, selector: #selector(sessionInterruptionEnded(_:)), name: Notification.Name("AVCaptureSessionInterruptionEndedNotification"), object: session)
    }
    @objc func sessionRuntimeError(_ notification: Notification) {
        
        guard let errorValue = notification.userInfo?[AVCaptureSessionErrorKey] as? NSError else {
            return
        }
        let error = AVError(_nsError: errorValue)
        
        print("Capture session runtime error: \(error)")
        
        /*
         Automatically try to restart the session running if media services were
         reset and the last start running succeeded. Otherwise, enable the user
         to try to resume the session running.
         */
        if error.code == .mediaServicesWereReset {
            sessionQueue.async {
                
                if self.isSessionRunning {
                    self.session.startRunning()
                    self.isSessionRunning = self.session.isRunning
                }
            }
        }
    }
    @objc func sessionWasInterrupted(_ notification: Notification) {
        
//         enable user to resume the session running
        guard let userInfoValue = notification.userInfo?[AVCaptureSessionInterruptionReasonKey] as AnyObject?,
            let reasonIntegerValue = userInfoValue.integerValue,
            let reason = AVCaptureSession.InterruptionReason(rawValue: reasonIntegerValue) else {
            return
        }
        print("Capture session was interrupted with reason \(reason)")
    }
    @objc func sessionInterruptionEnded(_ notification: Notification) {
        
        print("Capture session interruption ended")
    }
    func removeObservers() {
        
        NotificationCenter.default.removeObserver(self)
    }
    func exifOrientationFromDeviceOrientation() -> UInt32 {
        
        var exifOrientation: DeviceOrientation
        
        switch UIDevice.current.orientation {
        case .portraitUpsideDown:
            exifOrientation = .left0ColBottom
            
        case .landscapeLeft:
            exifOrientation = deviceCameraPosition == .front ? .bottom0ColRight : .top0ColLeft
            
        case .landscapeRight:
            exifOrientation = deviceCameraPosition == .front ? .top0ColLeft : .bottom0ColRight
            
        default:
            exifOrientation = .right0ColTop
        }
        return exifOrientation.rawValue
    }
}

// MARK: AVCapturePhotoCaptureDelegate functions
extension RealtimeViewController: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        
        guard let imageData = photo.fileDataRepresentation() else {
            return
        }
        image = UIImage(data: imageData)
        
        let tabBar = tabBarController as! TabBarViewController
        tabBar.image = image
    }
}

// MARK: AVCaptureVideoDataOutputSampleBufferDelegate functions
extension RealtimeViewController: AVCaptureVideoDataOutputSampleBufferDelegate {
    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer),
            let exitOrientation = CGImagePropertyOrientation(rawValue: exifOrientationFromDeviceOrientation()) else {
                return
        }
        var requestOptions: [VNImageOption : Any] = [:]
        
        if let cameraIntrinsicData = CMGetAttachment(sampleBuffer, key: kCMSampleBufferAttachmentKey_CameraIntrinsicMatrix, attachmentModeOut: nil) {
            requestOptions = [.cameraIntrinsics : cameraIntrinsicData]
        }
        
        let imageRequestHandler = VNImageRequestHandler(cvPixelBuffer: pixelBuffer, orientation: exitOrientation, options: requestOptions)
        
        do {
            try imageRequestHandler.perform(requests)
            
        } catch {
            print(error)
        }
    }
}

// MARK: other class methods
extension UIInterfaceOrientation {
    var videoOrientation: AVCaptureVideoOrientation? {
        
        switch self {
        case .portrait:
            return .portrait
            
        case .portraitUpsideDown:
            return .portraitUpsideDown
            
        case .landscapeLeft:
            return .landscapeLeft
            
        case .landscapeRight:
            return .landscapeRight
            
        default:
            return nil
        }
    }
}

extension UIDeviceOrientation {
    var videoOrientation: AVCaptureVideoOrientation? {
        
        switch self {
        case .portrait:
            return .portrait
            
        case .portraitUpsideDown:
            return .portraitUpsideDown
            
        case .landscapeLeft:
            return .landscapeRight
            
        case .landscapeRight:
            return .landscapeLeft
            
        default:
            return nil
        }
    }
    
}
