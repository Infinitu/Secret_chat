//
//  ChatLogCellTableViewCell.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 8..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "ChatLogCell.h"


@implementation ChatLogCell
NSDateFormatter *formatter;

UIFont *_nibfont;
- (void)awakeFromNib {
    _contentsLabelWrap = (UILabel*)[self viewWithTag:1];
    _contentsLabel = (UILabel*) [_contentsLabelWrap subviews][0];
    _timestampLabel = (UILabel*)[self viewWithTag:2];
    _nibfont = _contentsLabel.font;
    _tail = (UIImageView*)[self viewWithTag:4];
    _tail.image = [_tail.image imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];

    _contentsLabelWrap.layer.cornerRadius = 12;
    _contentsLabelWrap.layer.masksToBounds = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    // Configure the view for the selected state
}

+(CGSize)guessTextSize:(NSString *)text withWidth:(CGFloat)width{
    UIFont *font;
    if(_nibfont)
        font = _nibfont;
    else
        font = [UIFont systemFontOfSize:17];
    NSAttributedString *attributedText = [[NSAttributedString alloc] initWithString:text attributes:@{NSFontAttributeName:font}];
    CGRect rect = [attributedText boundingRectWithSize:(CGSize){width-80, CGFLOAT_MAX}
                                               options:NSStringDrawingUsesLineFragmentOrigin
                                               context:nil];
    
    return rect.size;
}

-(void)prepareView:(Message*)msg{
    [self prepareLayout:msg.text mine:msg.mine];
    self.contentsLabel.text = msg.text;
    if(msg.datetime<0){
        self.timestampLabel.text = @"pending";
        return;
    }
    if(!formatter){
        formatter = [[NSDateFormatter alloc]init];
        [formatter setDateFormat:@"hh:mm a"];
    }
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:msg.datetime/1000];
    NSString *stampStr = [formatter stringFromDate:date];
    self.timestampLabel.text = stampStr;
}


-(void)prepareLayout:(NSString*)str mine:(BOOL)isMine{
    
    CGSize size = [ChatLogCell guessTextSize:str withWidth:self.frame.size.width];
    CGRect bound = (CGRect){CGPointMake(12, 7),CGSizeMake((CGFloat) ceil(size.width), (CGFloat) ceil(size.height))};
    CGPoint labelPoint = CGPointMake((int)(isMine?self.frame.size.width-10/*tail to wall*/-6/*label to tail*/-size.width-24/*width paddindg*/:16), 0);
    CGSize labelSize = CGSizeMake(ceil(size.width+24),ceil(size.height+14));
    CGRect rect = (CGRect){labelPoint,labelSize};

    self.contentsLabel.numberOfLines = NSIntegerMax;
    [self.contentsLabelWrap setFrame:rect];
    [self.contentsLabel setFrame:bound];

    CGSize stampSize = self.timestampLabel.frame.size;
    CGRect stampRect = CGRectMake(isMine?labelPoint.x-70:labelPoint.x-5+labelSize.width+5,
                                  labelPoint.y+labelSize.height - stampSize.height,
                                  70,stampSize.height);




    if(self.tail){
        CGSize  tailSize = self.tail.frame.size;
        CGPoint tailPoint = CGPointMake(isMine?labelPoint.x+labelSize.width-tailSize.width+6/*tail to width*/:10, labelSize.height-tailSize.height);
        [self.tail setFrame:(CGRect){tailPoint,tailSize}];
    };



    [self.timestampLabel setFrame:stampRect];
    
}

@end
