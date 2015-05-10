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
    _contentsLabel = (UILabel*)[self viewWithTag:1];
    _timestampLabel = (UILabel*)[self viewWithTag:2];
    _nibfont = _contentsLabel.font;
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
        font = [UIFont systemFontOfSize:13];
    NSAttributedString *attributedText = [[NSAttributedString alloc] initWithString:text attributes:@{NSFontAttributeName:font}];
    CGRect rect = [attributedText boundingRectWithSize:(CGSize){width-90, CGFLOAT_MAX}
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
    CGRect rect = (CGRect){CGPointMake((int)(isMine?self.frame.size.width-10-size.width:0), 10),CGSizeMake(ceil(size.width),ceil(size.height))};
    
    self.contentsLabel.numberOfLines = NSIntegerMax;
    [self.contentsLabel setFrame:rect];
    
    
    CGSize stampSize = self.timestampLabel.frame.size;
    CGRect stampRect = CGRectMake(isMine?rect.origin.x-70:rect.origin.x-5+rect.size.width+5,
                                  rect.origin.y+rect.size.height - stampSize.height,
                                  70,stampSize.height);
    
    [self.timestampLabel setFrame:stampRect];
    
}

@end
