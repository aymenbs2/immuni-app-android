package org.immuni.android.ui.onboarding.fragments.profile

import android.os.Bundle
import android.view.View
import org.immuni.android.R
import org.immuni.android.db.entity.Gender
import org.immuni.android.ui.onboarding.OnboardingUserInfo
import com.bendingspoons.base.extensions.hideKeyboard
import com.bendingspoons.base.utils.ScreenUtils
import kotlinx.android.synthetic.main.onboarding_age_range_fragment.*
import kotlinx.android.synthetic.main.onboarding_gender_fragment.*
import kotlinx.android.synthetic.main.onboarding_gender_fragment.back
import kotlinx.android.synthetic.main.onboarding_gender_fragment.next
import kotlinx.android.synthetic.main.onboarding_gender_fragment.scrollView
import kotlinx.android.synthetic.main.onboarding_gender_fragment.topMask

class GenderFragment : ProfileContentFragment(R.layout.onboarding_gender_fragment) {
    override val nextButton: View
        get() = next

    override fun onResume() {
        super.onResume()
        this.view?.hideKeyboard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // on scrolling the top mask hide/show
        scrollView.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            updateTopMask(scrollY)
        }

        updateTopMask(scrollView.scrollY)

        female.setOnClickListener {
            validate(true)
        }

        male.setOnClickListener {
            validate(true)
        }

        back.setOnClickListener {
            viewModel.onPrevTap()
        }
    }

    private fun updateTopMask(scrollY: Int) {
        val dp = ScreenUtils.convertDpToPixels(requireContext(), 8).toFloat()
        topMask.alpha = 0f + scrollY/dp
    }

    override fun onUserInfoUpdate(userInfo: OnboardingUserInfo) {
        updateUI(userInfo.gender)
        validate(false)
    }

    private fun validate(updateModel: Boolean = true): Boolean {
        val valid = male.isChecked || female.isChecked
        nextButton.isEnabled = valid
        if (valid && updateModel) updateModel(if (male.isChecked) Gender.MALE else Gender.FEMALE)
        return valid
    }

    private fun updateModel(gender: Gender) {
        viewModel.userInfo()?.let {
            viewModel.updateUserInfo(it.copy(gender = gender))
        }
    }

    private fun updateUI(gender: Gender?) {
        male.isChecked = gender == Gender.MALE
        female.isChecked = gender == Gender.FEMALE
    }
}
