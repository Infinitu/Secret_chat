//
// Created by 김창규 on 15. 5. 12..
// Copyright (c) 2015 the.accidental.billionaire. All rights reserved.
//

#import "RegisterViewController.h"
#import <MobileCoreServices/MobileCoreServices.h>
#import "Version.h"
#import "CKJsonParser.h"
#import "UserData.h"
#import "AppDelegate.h"

@interface RegisterViewController ()
@property (weak, nonatomic) IBOutlet UITextField *nickeNameField;
@property (weak, nonatomic) IBOutlet UIImageView *ProfileView;
@property (weak, nonatomic) IBOutlet UITextField *birthYearField;
@property (weak, nonatomic) IBOutlet UISegmentedControl *sexSegment;
@property (weak, nonatomic) IBOutlet UISegmentedControl *bloodTypeSegment;
@property (weak, nonatomic) IBOutlet UIVisualEffectView *waitingView;

@end

@interface NSURLRequest (DummyInterface)
+ (BOOL)allowsAnyHTTPSCertificateForHost:(NSString*)host;
+ (void)setAllowsAnyHTTPSCertificate:(BOOL)allow forHost:(NSString*)host;
@end

@implementation RegisterViewController
-(void)viewDidLoad{

}

- (IBAction)yearDidBeginEdit:(id)sender {
    self.birthYearField.text = @"";
}
- (IBAction)yearChanged:(id)sender {
    if(self.birthYearField.text.length==4)
       [self.birthYearField resignFirstResponder];
}
- (IBAction)yearStepperClicked:(UIStepper*)sender {
    self.birthYearField.text = [NSString stringWithFormat:@"%d",(int)sender.value ];
}
- (IBAction)nicknameEditEnded:(UITextField*)sender {
    [sender resignFirstResponder];
}
- (IBAction)profileImageTakePhoto:(id)sender {
    [self startCameraControllerFromViewController:self usingDelegate:self];
}
- (IBAction)profileImageChooseLib:(id)sender {
    [self startMediaBrowserFromViewController:self usingDelegate:self];
}

- (IBAction)finishToMakeProfile:(id)sender {
    self.waitingView.hidden = false;
//    self.waitingView.alpha = 0;
    self.waitingView.hidden = false;
    [UIView animateWithDuration:1 animations:^{
        self.waitingView.alpha = 1.0;
    }];



    /*
    “/join” : 가입 화면
   method : POST (가입 신청)
송신 : nickName, birthYear, gender, bloodType (서버에 송신하면서 스마트폰이 저장)
(만약 client에 accessToken있다면 가입 화면 말고 채팅 화면으로 넘어감)
수신 : accessToken (accessToken 을 스마트폰이 저장하고 앞으로 다른 정보 수신 시 accessToken 전달
     */

    NSString *name  =  self.nickeNameField.text;
    int birth = self.birthYearField.text.intValue;
    if(birth<1800 || birth >2015)
        birth = 1990;
    NSString *blood  = [self.bloodTypeSegment titleForSegmentAtIndex:(NSUInteger)self.bloodTypeSegment.selectedSegmentIndex];
    NSNumber *sex  = self.bloodTypeSegment.selectedSegmentIndex==0?@(1):@(2);


    UIDevice* device = [UIDevice currentDevice];
    NSString* uuid = [[device identifierForVendor] UUIDString];

    NSDictionary *form = @{
            @"deviceId":uuid,
            @"nickName":name,
            @"age": [@(2015-birth+1) stringValue],
            @"bloodType":blood,
            @"gender":sex
    };

    NSURL *joinURL =
            [[NSURL alloc] initWithScheme:DEFAULT_API_SCHEME
                                     host:[NSString stringWithFormat:@"%@:%d",DEFAULT_API_HOST,DEFAULT_API_PORT]
                                     path:@"/join"];

    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:joinURL];

    req.HTTPMethod = @"POST";

    NSMutableData *body = [NSMutableData data];
    UIImage *imageToUpload = self.ProfileView.image;
    CGFloat scale = 120.0f/imageToUpload.size.width;
    if(scale<1)
        imageToUpload = [self compressForUpload:imageToUpload scale:scale];

    NSString *boundary = @"SecretChat";
    NSString *contentType = [NSString stringWithFormat:@"multipart/mixed; boundary=%@", boundary];
    [req addValue:contentType forHTTPHeaderField:@"Content-Type"];


    for(NSString *key in form.allKeys){
        [body appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"\r\n\r\n%@",key,form[key]] dataUsingEncoding:NSUTF8StringEncoding]];
    }




    [body appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[@"Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[@"Content-Type: application/octet-stream\r\n\r\n" dataUsingEncoding:NSUTF8StringEncoding]];

    [body appendData:UIImagePNGRepresentation(imageToUpload)];

    [body appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];

    [req setHTTPBody:body];



    [NSURLRequest setAllowsAnyHTTPSCertificate:YES forHost:DEFAULT_API_HOST];
    [NSURLConnection sendAsynchronousRequest:req
                                       queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *res, NSData *data, NSError *err){
                               NSHTTPURLResponse *response = (NSHTTPURLResponse *) res;
                               if(response.statusCode!=200)
                                   return [self failedToRegister];
                               NSMutableData *mdata = [NSMutableData dataWithData:data];
                               uint8_t nullChar = '\0';
                               [mdata appendBytes: &nullChar length:1];
                               NSString *token = [NSString stringWithUTF8String:mdata.bytes];
                              [self succeedToRegisterWithForm:form withAccessToken:token];
                           }];
}

- (UIImage *)compressForUpload:(UIImage *)original scale:(CGFloat)scale
{
    // Calculate new size given scale factor.
    CGSize originalSize = original.size;
    CGSize newSize = CGSizeMake(originalSize.width * scale, originalSize.height * scale);

    // Scale the original image to match the new size.
    UIGraphicsBeginImageContext(newSize);
    [original drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
    UIImage* compressedImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();

    return compressedImage;
}

-(void)failedToRegister{
    UIAlertView *theAlert = [[UIAlertView alloc] initWithTitle:@"Oops"
                                                       message:@"User Registration had been failed. Please try agin."
                                                      delegate:self
                                             cancelButtonTitle:@"Ok"
                                             otherButtonTitles:nil];
    [theAlert show];
    self.waitingView.hidden = true;
    [UIView animateWithDuration:1 animations:^{
        self.waitingView.alpha = 0.0;
    }];
}

-(void)succeedToRegisterWithForm:(NSDictionary*)form withAccessToken:(NSString*)token{
    UserData *data = [[UserData alloc] init];
    data.accessToken=token;
    data.deviceId = form[@"deviceId"];


    Friend *res = [[Friend alloc]init];
    res.address = token;
    res.nickname = form[@"nickName"];
    res.profileImg = form[@"profileImg"];
    res.bloodType = form[@"bloodType"];
    res.age     = [((NSNumber*) form[@"age"]) intValue];
    res.sex     = [((NSNumber*) form[@"gender"]) intValue];

    data.profile = res;

    [data saveToUserDefault:[NSUserDefaults standardUserDefaults]];
    [(AppDelegate *)[[UIApplication sharedApplication] delegate] initializeWithUserData:data];

}
- (BOOL) startMediaBrowserFromViewController: (UIViewController*) controller
                               usingDelegate: (id <UIImagePickerControllerDelegate,
                                               UINavigationControllerDelegate>) delegate {
    return [self startPickerFromViewController:controller
                                 usingDelegate:delegate
                               usingSourceType:UIImagePickerControllerSourceTypeSavedPhotosAlbum];
}
- (BOOL) startCameraControllerFromViewController: (UIViewController*) controller
                                   usingDelegate: (id <UIImagePickerControllerDelegate,
                                                   UINavigationControllerDelegate>) delegate {
    return [self startPickerFromViewController:controller
                                 usingDelegate:delegate
                               usingSourceType:UIImagePickerControllerSourceTypeCamera];
}

- (BOOL) startPickerFromViewController: (UIViewController*) controller
                         usingDelegate: (id <UIImagePickerControllerDelegate,
                                                   UINavigationControllerDelegate>) delegate
                       usingSourceType:(UIImagePickerControllerSourceType)type {
    
    if (![UIImagePickerController isSourceTypeAvailable:type]
        || (delegate == nil)
        || (controller == nil))
        return NO;
    
    
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    picker.sourceType = type;
    
    // Displays a control that allows the user to choose picture or
    // movie capture, if both are available:
    picker.mediaTypes = @[(NSString*)kUTTypeImage];
    
    // Hides the controls for moving & scaling pictures, or for
    // trimming movies. To instead show the controls, use YES.
    picker.allowsEditing = YES;
    
    picker.delegate = delegate;
    
    [controller presentViewController:picker animated:YES completion:nil];
    return YES;
}

- (void) imagePickerController: (UIImagePickerController *) picker
 didFinishPickingMediaWithInfo: (NSDictionary *) info {
    
    NSString *mediaType = info[UIImagePickerControllerMediaType];
    UIImage *originalImage, *editedImage, *imageToUse;
    
    // Handle a still image picked from a photo album
    if (CFStringCompare ((CFStringRef) mediaType,   kUTTypeImage, 0)
        == kCFCompareEqualTo) {
        
        editedImage = (UIImage *) info[UIImagePickerControllerEditedImage];
        originalImage = (UIImage *) info[UIImagePickerControllerOriginalImage];
        
        if (editedImage) {
            imageToUse = editedImage;
        } else {
            imageToUse = originalImage;
        }
        self.ProfileView.image = imageToUse;
    }
    
    // Handle a movied picked from a photo album
    if (CFStringCompare ((CFStringRef) mediaType, kUTTypeMovie, 0)
        == kCFCompareEqualTo) {
        
        NSString *moviePath = [info[UIImagePickerControllerMediaURL] path];
        
        // Do something with the picked movie available at moviePath
    }
    
    [picker dismissViewControllerAnimated:YES completion:nil];
}

@end
