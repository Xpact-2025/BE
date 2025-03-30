package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.dto.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.entity.*;
import com.itstime.xpact.domain.experience.repository.CategoryRepository;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    private final SecurityProvider securityProvider;

    /**
     * Experience Create 서비스 로직 : FormType에 따라 양식이 바뀜 & Status에 따라 저장방식 다름
     */
    @Transactional
    public void create(ExperienceCreateRequestDto createRequestDto) throws CustomException {
        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        Experience experience;

        // category 조회
        List<Category> categories = createRequestDto.getExperienceCategories()
                .stream()
                .map(categoryName -> categoryRepository.findByName(categoryName)
                        .orElseThrow(() -> CustomException.of(ErrorCode.INVALID_CATEGORY)))
                .toList();


        // experience entity 생성 (experience 형식에 따라 StarForm, SimpleForm 결정)
        if(createRequestDto.getFormType().equals(FormType.SIMPLE_FORM)) {
            experience = SimpleForm.from(createRequestDto);
        } else if(createRequestDto.getFormType().equals(FormType.STAR_FORM)) {
            experience = StarForm.from(createRequestDto);
        } else throw new CustomException(ErrorCode.INVALID_FORMTYPE);


        // ExperienceCategory생성 및 연관관계 설정
        List<ExperienceCategory> experienceCategories = categories
                .stream()
                .map(category -> ExperienceCategory.from(experience, category))
                .toList();

        // entity간 연관관계 설정
        experience.addMember(member);
        experience.addExperienceCategories(experienceCategories);

        // 저장 방식 결정
        if(experience.getStatus().equals(Status.STASH)) {
            // TODO 추후 stash 로직 구현해야함 (일단 저장은 하고)
            experienceRepository.save(experience);
        } else if(experience.getStatus().equals(Status.SAVE)) {
            // TODO 추후 save 로직 구현해야함(대시보드 업데이트) (일단 저장은 하고)
            experienceRepository.save(experience);
        } else {
            throw new ExperienceException(ErrorCode.INVALID_STATUS);
        }
    }

    @Transactional(readOnly = true)
    public List<ThumbnailExperienceReadResponseDto> readAll() throws CustomException {
        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        return experienceRepository.findAllByMember(member)
                .stream()
                .map(experience -> {
                    List<String> categoryNames = experience.getExperienceCategories().stream()
                            .map(experienceCategory -> experienceCategory.getCategory().getName())
                            .toList();
                    return ThumbnailExperienceReadResponseDto.of(experience, categoryNames);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public DetailExperienceReadResponseDto read(Long experienceId) throws CustomException {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));
        List<Category> categories = experience.getExperienceCategories()
                .stream()
                .map(ExperienceCategory::getCategory)
                .toList();
        return DetailExperienceReadResponseDto.from(experience, categories);
    }

    @Transactional
    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) throws CustomException {
        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        // experience 조회
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        // category 조회
        List<Category> categories = updateRequestDto.getExperienceCategories()
                .stream()
                .map(categoryName -> categoryRepository.findByName(categoryName)
                        .orElseThrow(() -> CustomException.of(ErrorCode.INVALID_CATEGORY)))
                .toList();

        Experience updatedExperiecne;
        // 경험 유형이 변경되었는지 체크
        if(hasFormTypeChanged(experience, updateRequestDto)) {
            // 유형이 변경되었으면 새로 Entity생성 (기존의 엔티티는 삭제)
            experienceRepository.delete(experience);
            updatedExperiecne = createUpdatedExperience(updateRequestDto);
        } else {
            // 유형이 변경되지 않았으면 기존의 Entity에서 수정사항만 반영
            updateExperience(experience, updateRequestDto);
            updatedExperiecne = experience;
        }

        // experienceCategory 연관관계 생성 (experience 생성이 선행되어야함)
        List<ExperienceCategory> experienceCategories = categories
                .stream()
                .map(category -> ExperienceCategory.from(updatedExperiecne, category))
                .toList();

        updatedExperiecne.addMember(member);
        updatedExperiecne.addExperienceCategories(experienceCategories);

        // 저장 방식 결정
        if(updatedExperiecne.getStatus().equals(Status.STASH)) {
            // TODO 추후 stash 로직 구현해야함 (일단 저장은 하고)
            experienceRepository.save(updatedExperiecne);
        } else if(experience.getStatus().equals(Status.SAVE)) {
            // TODO 추후 save 로직 구현해야함(대시보드 업데이트) (일단 저장은 하고)
            experienceRepository.save(updatedExperiecne);
        } else {
            throw new CustomException(ErrorCode.INVALID_STATUS);
        }
    }

    /**
     * 기존의 Experience객체는 delete하고 updateRequestDto를 통해 새로운 Experience객체를 생성 <br>
     * (이때 FormType에 맞도록 return)
     */
    private Experience createUpdatedExperience(ExperienceUpdateRequestDto updateRequestDto) throws CustomException {
        if(updateRequestDto.getFormType().equals(FormType.SIMPLE_FORM)) {
            return SimpleForm.from(updateRequestDto);
        } else if(updateRequestDto.getFormType().equals(FormType.STAR_FORM)) {
            return StarForm.from(updateRequestDto);
        } else {
            throw new CustomException(ErrorCode.INVALID_FORMTYPE);
        }
    }

    /**
     * 기존의 Experience를 updateRequestDto에 맞게 수정하는 메서드, 기존의 experience객체 사용 <br>
     * (FormType형식에 맞게 update 진행)
     */
    private void updateExperience(Experience experience, ExperienceUpdateRequestDto updateRequestDto) throws CustomException {
        if(updateRequestDto.getFormType().equals(FormType.SIMPLE_FORM)) {
            experience.update(updateRequestDto);
        } else if(updateRequestDto.getFormType().equals(FormType.STAR_FORM)) {
            experience.update(updateRequestDto);
        } else {
            throw new CustomException(ErrorCode.INVALID_FORMTYPE);
        }
    }

    /**
     * 기존의 experience와 updateRequestDto의 유형이 변경되는지 체크 <br>
     * 유형이 변경되었다면 return true <br>
     * 유형이 변경되지 않았다면 return false <br>
     */
    private Boolean hasFormTypeChanged(Experience experience, ExperienceUpdateRequestDto updateRequestDto) {
        if(experience instanceof StarForm && updateRequestDto.getFormType().equals(FormType.STAR_FORM)) {
            return false;
        } else if(experience instanceof SimpleForm && updateRequestDto.getFormType().equals(FormType.SIMPLE_FORM)) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public void delete(Long experienceId) throws CustomException {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));
        experienceRepository.delete(experience);
    }
}
